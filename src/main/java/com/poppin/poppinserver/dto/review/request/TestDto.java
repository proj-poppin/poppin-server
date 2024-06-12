package com.poppin.poppinserver.dto.review.request;

import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record TestDto(
        List<MultipartFile> images,

        @Valid
        CreateReviewDto createReviewDto
) {
}
