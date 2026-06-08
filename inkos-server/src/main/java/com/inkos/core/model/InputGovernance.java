package com.inkos.core.model;

import java.util.List;
import java.util.Map;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

record ChapterMemo(
    int chapter,
    @NotBlank @Size(max = 50) String goal,
    boolean isGoldenOpening,
    @NotBlank String body,
    List<String> threadRefs
) {}

record ChapterIntent(
    int chapter,
    @NotBlank String goal,
    String outlineNode,
    String arcContext,
    List<String> mustKeep,
    List<String> mustAvoid,
    List<String> styleEmphasis
) {}

record ContextSource(
    @NotBlank String source,
    @NotBlank String reason,
    String excerpt
) {}

record ContextPackage(
    int chapter,
    List<ContextSource> selectedContext
) {}

record RuleLayer(
    @NotBlank String id,
    @NotBlank String name,
    int precedence,
    String scope
) {
    static final String SCOPE_GLOBAL = "global";
    static final String SCOPE_BOOK = "book";
    static final String SCOPE_ARC = "arc";
    static final String SCOPE_LOCAL = "local";
}

record OverrideEdge(
    @NotBlank String from,
    @NotBlank String to,
    boolean allowed,
    @NotBlank String scope
) {}

record ActiveOverride(
    @NotBlank String from,
    @NotBlank String to,
    @NotBlank String target,
    @NotBlank String reason
) {}

record RuleStackSections(
    List<String> hard,
    List<String> soft,
    List<String> diagnostic
) {}

record RuleStack(
    List<RuleLayer> layers,
    RuleStackSections sections,
    List<OverrideEdge> overrideEdges,
    List<ActiveOverride> activeOverrides
) {
    static final String DEFAULT_MODE_V2 = "v2";
    static final String DEFAULT_MODE_LEGACY = "legacy";
}

record ChapterTrace(
    int chapter,
    List<String> plannerInputs,
    List<String> composerInputs,
    List<String> selectedSources,
    List<String> notes
) {}
