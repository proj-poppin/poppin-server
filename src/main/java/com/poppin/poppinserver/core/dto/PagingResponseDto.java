package com.poppin.poppinserver.core.dto;

import io.micrometer.common.lang.Nullable;
import lombok.Builder;

@Builder
public record PagingResponseDto<T>(
        @Nullable T items,
        @Nullable PageInfoDto pageInfo
) {
    public static <T> PagingResponseDto<T> fromEntityAndPageInfo(T data, PageInfoDto pageInfoDto){
        return new PagingResponseDto<T>(data, pageInfoDto);
    }
}
