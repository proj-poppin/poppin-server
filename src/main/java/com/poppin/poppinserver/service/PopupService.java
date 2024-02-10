package com.poppin.poppinserver.service;

import com.poppin.poppinserver.repository.PopupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PopupService {
    private final PopupRepository popupRepository;


}
