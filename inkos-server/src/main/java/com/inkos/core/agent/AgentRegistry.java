package com.inkos.core.agent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AgentRegistry {

  private final Map<String, Agent> agents = new ConcurrentHashMap<>();
  private final List<Agent> pipelineOrder = new ArrayList<>();

  public AgentRegistry() {
    register(new RadarAgent());
    register(new PlannerAgent());
    register(new ArchitectAgent());
    register(new WriterAgent());
    register(new ObserverAgent());
    register(new ReflectorAgent());
    register(new NormalizerAgent());
    register(new AuditorAgent());
    register(new ReviserAgent());
  }

  public void register(Agent agent) {
    agents.put(agent.getName(), agent);
    pipelineOrder.add(agent);
  }

  public Agent getAgent(String name) {
    Agent agent = agents.get(name);
    if (agent == null) {
      throw new IllegalArgumentException("Unknown agent: " + name);
    }
    return agent;
  }

  public Map<String, Agent> getAllAgents() {
    return Collections.unmodifiableMap(agents);
  }

  public List<Agent> getPipelineAgents() {
    return Collections.unmodifiableList(pipelineOrder);
  }

  /**
   * Returns the agents for the full pipeline mode.
   * All 9 agents run in sequence.
   */
  public List<Agent> getFullPipelineAgents() {
    return List.of(
      getAgent("radar"),
      getAgent("planner"),
      getAgent("writer"),
      getAgent("observer"),
      getAgent("reflector"),
      getAgent("normalizer"),
      getAgent("auditor"),
      getAgent("reviser")
    );
  }

  /**
   * Returns the agents for legacy mode.
   * Only writer + normalizer, skip plan/audit/revise.
   */
  public List<Agent> getLegacyPipelineAgents() {
    return List.of(
      getAgent("writer"),
      getAgent("normalizer")
    );
  }

  /**
   * Returns write-cycle agents (post-planning).
   * Writer → Observer → Reflector → Normalizer.
   */
  public List<Agent> getWriteCycleAgents() {
    return List.of(
      getAgent("writer"),
      getAgent("observer"),
      getAgent("reflector"),
      getAgent("normalizer")
    );
  }

  /**
   * Returns audit-revise cycle agents.
   * Auditor → (loop) Reviser → Auditor.
   */
  public List<Agent> getAuditReviseCycleAgents() {
    return List.of(
      getAgent("auditor"),
      getAgent("reviser")
    );
  }

  public int size() {
    return agents.size();
  }
}
