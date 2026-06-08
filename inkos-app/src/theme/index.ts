export interface ThemeColors {
  textPrimary: string
  textSecondary: string
  textTertiary: string
  textInverse: string
  bgPrimary: string
  bgSecondary: string
  bgTertiary: string
  bgCard: string
  accentPrimary: string
  accentSecondary: string
  accentWarm: string
  accentDanger: string
  accentSuccess: string
  borderLight: string
  borderMedium: string
}

export interface Theme {
  name: string
  colors: ThemeColors
  isDark: boolean
}

export const lightTheme: Theme = {
  name: 'light',
  isDark: false,
  colors: {
    textPrimary: '#1a1a2e',
    textSecondary: '#4a4a6a',
    textTertiary: '#8e8ea0',
    textInverse: '#ffffff',
    bgPrimary: '#f5f5f8',
    bgSecondary: '#ffffff',
    bgTertiary: '#e8e8ee',
    bgCard: '#ffffff',
    accentPrimary: '#3b82f6',
    accentSecondary: '#6366f1',
    accentWarm: '#f59e0b',
    accentDanger: '#ef4444',
    accentSuccess: '#10b981',
    borderLight: '#e5e7eb',
    borderMedium: '#d1d5db',
  },
}

export const darkTheme: Theme = {
  name: 'dark',
  isDark: true,
  colors: {
    textPrimary: '#e8e8f0',
    textSecondary: '#a0a0b8',
    textTertiary: '#6b6b80',
    textInverse: '#1a1a2e',
    bgPrimary: '#0f0f1a',
    bgSecondary: '#1a1a2e',
    bgTertiary: '#252540',
    bgCard: '#1e1e35',
    accentPrimary: '#60a5fa',
    accentSecondary: '#818cf8',
    accentWarm: '#fbbf24',
    accentDanger: '#f87171',
    accentSuccess: '#34d399',
    borderLight: '#2a2a45',
    borderMedium: '#3a3a55',
  },
}

export function getSystemTheme(): 'light' | 'dark' {
  if (uni && uni.getSystemInfoSync) {
    const sys = uni.getSystemInfoSync()
    if (sys && 'theme' in sys) {
      return (sys as any).theme === 'dark' ? 'dark' : 'light'
    }
  }
  return 'light'
}
