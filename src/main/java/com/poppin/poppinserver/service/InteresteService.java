package com.poppin.poppinserver.service;

import com.poppin.poppinserver.domain.Intereste;
import com.poppin.poppinserver.domain.Popup;
import com.poppin.poppinserver.domain.User;
import com.poppin.poppinserver.dto.Intereste.response.AddInteresteDto;
import com.poppin.poppinserver.exception.CommonException;
import com.poppin.poppinserver.exception.ErrorCode;
import com.poppin.poppinserver.repository.PopupRepository;
import com.poppin.poppinserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InteresteService {
    private final UserRepository userRepository;
    private final PopupRepository popupRepository;

    public AddInteresteDto userAddIntereste(Long userId, Long popupId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        Popup popup = popupRepository.findById(popupId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_POPUP));

        Intereste intereste = Intereste.builder()
                .user(user)
                .popup(popup)
                .build();

        // 3. 즐겨찾기 저장
        favoriteRepository.save(favorite);

        // 4. 연관 관계 업데이트 (필요한 경우)
        // 이 부분은 JPA가 자동으로 처리할 수도 있으며, 명시적으로 관리해야 할 수도 있습니다.
        user.getFavorites().add(favorite);
        store.getFavorites().add(favorite);
    }
}
