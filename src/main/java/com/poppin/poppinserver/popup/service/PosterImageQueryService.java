package com.poppin.poppinserver.popup.service;

import com.poppin.poppinserver.popup.repository.PosterImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PosterImageQueryService {
    private final PosterImageRepository posterImageRepository;
}
