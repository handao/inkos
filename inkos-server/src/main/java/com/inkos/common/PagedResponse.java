package com.inkos.common;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class PagedResponse<T> {
    private final List<T> content;
    private final int page;
    private final int size;
    private final long total;
    private final int totalPages;
}
