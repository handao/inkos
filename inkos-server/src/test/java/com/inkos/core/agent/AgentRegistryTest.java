package com.inkos.core.agent;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class AgentRegistryTest {

  private AgentRegistry registry;

  @BeforeEach
  void setUp() {
    registry = new AgentRegistry();
  }

  @Test
  void constructor_shouldRegisterAllAgents() {
    assertEquals(9, registry.size());
  }

  @Test
  void getAgent_shouldReturnAgentByName() {
    var writer = registry.getAgent("writer");
    assertNotNull(writer);
    assertEquals("writer", writer.getName());
  }

  @Test
  void getAgent_shouldThrowForUnknown() {
    assertThrows(IllegalArgumentException.class, () -> registry.getAgent("nonexistent"));
  }

  @Test
  void getFullPipelineAgents_shouldReturnEightAgents() {
    var pipeline = registry.getFullPipelineAgents();
    assertEquals(8, pipeline.size());
    var names = pipeline.stream().map(Agent::getName).toList();
    assertEquals(List.of("radar", "planner", "writer", "observer", "reflector", "normalizer", "auditor", "reviser"), names);
  }

  @Test
  void getLegacyPipelineAgents_shouldReturnWriterAndNormalizer() {
    var legacy = registry.getLegacyPipelineAgents();
    assertEquals(2, legacy.size());
    var names = legacy.stream().map(Agent::getName).toList();
    assertEquals(List.of("writer", "normalizer"), names);
  }

  @Test
  void getWriteCycleAgents_shouldReturnFourAgents() {
    var cycle = registry.getWriteCycleAgents();
    assertEquals(4, cycle.size());
    var names = cycle.stream().map(Agent::getName).toList();
    assertEquals(List.of("writer", "observer", "reflector", "normalizer"), names);
  }

  @Test
  void getAuditReviseCycleAgents_shouldReturnTwoAgents() {
    var cycle = registry.getAuditReviseCycleAgents();
    assertEquals(2, cycle.size());
    var names = cycle.stream().map(Agent::getName).toList();
    assertEquals(List.of("auditor", "reviser"), names);
  }

  @Test
  void register_shouldAddNewAgent() {
    var testAgent = new Agent() {
      @Override public String getName() { return "test-agent"; }
      @Override public String getRole() { return "test"; }
      @Override public java.util.concurrent.CompletableFuture<AgentResult> execute(
        com.inkos.core.pipeline.AgentContext ctx, AgentInput input) {
        return java.util.concurrent.CompletableFuture.completedFuture(AgentResult.ok("test"));
      }
    };
    registry.register(testAgent);
    assertEquals(10, registry.size());
    assertSame(testAgent, registry.getAgent("test-agent"));
  }

  @Test
  void getAllAgents_shouldReturnAll() {
    var all = registry.getAllAgents();
    assertEquals(9, all.size());
    assertTrue(all.containsKey("writer"));
    assertTrue(all.containsKey("radar"));
    assertTrue(all.containsKey("planner"));
    assertTrue(all.containsKey("architect"));
    assertTrue(all.containsKey("observer"));
    assertTrue(all.containsKey("reflector"));
    assertTrue(all.containsKey("normalizer"));
    assertTrue(all.containsKey("auditor"));
    assertTrue(all.containsKey("reviser"));
  }

  @Test
  void getPipelineAgents_shouldIncludeArchitect() {
    var all = registry.getPipelineAgents();
    var names = all.stream().map(Agent::getName).toList();
    assertEquals(9, names.size());
    assertTrue(names.contains("architect"));
  }

  @Test
  void allAgents_shouldHaveNonEmptyRoles() {
    registry.getAllAgents().values().forEach(agent ->
      assertAll(
        () -> assertNotNull(agent.getRole(), agent.getName() + " role should not be null"),
        () -> assertFalse(agent.getRole().isBlank(), agent.getName() + " role should not be blank")
      )
    );
  }

  @Test
  void allAgentNames_shouldBeUnique() {
    var names = registry.getAllAgents().keySet();
    assertEquals(names.size(), names.stream().distinct().count());
  }

  @Test
  void getAgent_shouldReturnSameInstance() {
    var a1 = registry.getAgent("writer");
    var a2 = registry.getAgent("writer");
    assertSame(a1, a2);
  }

  @Test
  void pipelineOrder_shouldMatchConstructorRegistration() {
    var order = registry.getPipelineAgents().stream().map(Agent::getName).toList();
    assertEquals(List.of(
      "radar", "planner", "architect", "writer",
      "observer", "reflector", "normalizer", "auditor", "reviser"
    ), order);
  }
}
