package com.poppin.poppinserver.service;

import com.poppin.poppinserver.domain.Popup;
import com.poppin.poppinserver.domain.User;
import com.poppin.poppinserver.dto.Intereste.response.AddInteresteDto;
import com.poppin.poppinserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    pri

    public AddInteresteDto userAddIntereste(Long userId, Long popupId){
        // 1. 유저와 가게 엔티티 찾기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Popup popup = po
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("Store not found"));

        // 2. 즐겨찾기 엔티티 생성
        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setStore(store);

        // 3. 즐겨찾기 저장
        favoriteRepository.save(favorite);

        // 4. 연관 관계 업데이트 (필요한 경우)
        // 이 부분은 JPA가 자동으로 처리할 수도 있으며, 명시적으로 관리해야 할 수도 있습니다.
        user.getFavorites().add(favorite);
        store.getFavorites().add(favorite);
    }
}
