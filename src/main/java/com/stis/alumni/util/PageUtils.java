package com.stis.alumni.util;

import com.stis.alumni.dto.PageResponse;
import org.springframework.data.domain.Page;

public final class PageUtils {

    private PageUtils() {
    }

    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }
}
