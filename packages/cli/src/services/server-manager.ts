import { spawn, type ChildProcess } from 'node:child_process';
import { writeFile, readFile, unlink, access } from 'node:fs/promises';
import { join } from 'node:path';
import { setTimeout as sleep } from 'node:timers/promises';

const PID_FILE = '.inkos-server.pid';
const SERVER_DIR = 'inkos-server';
const HEALTH_URL = 'http://localhost:8080/api/v1/auth/me';
const DEFAULT_TIMEOUT_MS = 60_000;

let serverProcess: ChildProcess | null = null;

export function getPidPath(projectRoot: string): string {
  return join(projectRoot, PID_FILE);
}

export function getServerDir(projectRoot: string): string {
  return join(projectRoot, SERVER_DIR);
}

export async function startServer(projectRoot: string): Promise<void> {
  const pidPath = getPidPath(projectRoot);

  const already = await isRunning(projectRoot);
  if (already) {
    throw new Error('Server is already running');
  }

  const serverDir = getServerDir(projectRoot);
  try {
    await access(serverDir);
  } catch {
    throw new Error(`Server directory not found: ${serverDir}`);
  }

  // Check for mvnw
  const isWindows = process.platform === 'win32';
  const mvnw = join(serverDir, isWindows ? 'mvnw.cmd' : './mvnw');
  try {
    await access(mvnw);
  } catch {
    throw new Error(`Maven wrapper not found at ${mvnw}. Run ./mvnw in ${serverDir} first.`);
  }

  return new Promise<void>((resolve, reject) => {
    const proc = spawn(mvnw, ['spring-boot:run'], {
      cwd: serverDir,
      stdio: ['ignore', 'pipe', 'pipe'],
      detached: false,
    });

    serverProcess = proc;

    let started = false;
    const timeout = setTimeout(() => {
      if (!started) {
        proc.kill();
        reject(new Error('Server start timed out'));
      }
    }, DEFAULT_TIMEOUT_MS);

    proc.stdout?.on('data', (data: Buffer) => {
      const text = data.toString();
      if (!started && text.includes('Started InkOsApplication')) {
        started = true;
        clearTimeout(timeout);
        writeFile(pidPath, String(proc.pid), 'utf-8')
          .then(() => resolve())
          .catch(reject);
      }
    });

    proc.stderr?.on('data', (data: Buffer) => {
      const text = data.toString();
      if (!started && text.includes('Started InkOsApplication')) {
        started = true;
        clearTimeout(timeout);
        writeFile(pidPath, String(proc.pid), 'utf-8')
          .then(() => resolve())
          .catch(reject);
      }
    });

    proc.on('error', (err) => {
      clearTimeout(timeout);
      if (!started) {
        reject(new Error(`Failed to start server: ${err.message}`));
      }
    });

    proc.on('exit', (code) => {
      serverProcess = null;
      if (!started) {
        clearTimeout(timeout);
        reject(new Error(`Server process exited with code ${code} before starting`));
      }
    });
  });
}

export async function stopServer(projectRoot: string): Promise<void> {
  const pidPath = getPidPath(projectRoot);

  if (serverProcess) {
    serverProcess.kill('SIGTERM');
    serverProcess = null;
  }

  try {
    const pidStr = await readFile(pidPath, 'utf-8');
    const pid = parseInt(pidStr.trim(), 10);
    try {
      process.kill(pid, 'SIGTERM');
    } catch {
      // Process may already be gone
    }
    try { await unlink(pidPath); } catch { /* ignore */ }
  } catch {
    // No PID file — try to find any process on port 8080
    try { await unlink(pidPath); } catch { /* ignore */ }
    throw new Error('No server PID file found. Server may not be running.');
  }
}

export async function isRunning(projectRoot: string): Promise<boolean> {
  const pidPath = getPidPath(projectRoot);
  try {
    const pidStr = await readFile(pidPath, 'utf-8');
    const pid = parseInt(pidStr.trim(), 10);
    try {
      process.kill(pid, 0);
      return true;
    } catch {
      try { await unlink(pidPath); } catch { /* ignore */ }
      return false;
    }
  } catch {
    return false;
  }
}

export async function waitForReady(timeoutMs: number = DEFAULT_TIMEOUT_MS): Promise<boolean> {
  const deadline = Date.now() + timeoutMs;
  while (Date.now() < deadline) {
    try {
      const controller = new AbortController();
      const t = setTimeout(() => controller.abort(), 3000);
      const response = await fetch(HEALTH_URL, { signal: controller.signal });
      clearTimeout(t);
      if (response.ok) return true;
    } catch {
      // Server not ready yet
    }
    await sleep(2000);
  }
  return false;
}
