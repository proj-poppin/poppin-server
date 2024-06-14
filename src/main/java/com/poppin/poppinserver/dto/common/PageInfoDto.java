package com.poppin.poppinserver.dto.common;

import lombok.Builder;
import org.springframework.data.domain.Page;

@Builder
public record PageInfoDto(
        Integer page,
        Integer size,
        Integer totalPages
) {
    public static PageInfoDto fromPageInfo(Page<?> result) {

        return PageInfoDto.builder()
                .page(result.getPageable().getPageNumber())
                .size(result.getPageable().getPageSize())
                .totalPages(result.getTotalPages())
                .build();
    }
}

