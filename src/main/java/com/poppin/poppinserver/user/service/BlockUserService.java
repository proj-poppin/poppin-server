package com.poppin.poppinserver.user.service;

import com.poppin.poppinserver.core.exception.CommonException;
import com.poppin.poppinserver.core.exception.ErrorCode;
import com.poppin.poppinserver.user.domain.BlockedUser;
import com.poppin.poppinserver.user.domain.User;
import com.poppin.poppinserver.user.repository.BlockedUserQueryRepository;
import com.poppin.poppinserver.user.repository.UserQueryRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BlockUserService {
    private final BlockedUserQueryRepository blockedUserQueryRepository;
    private final UserQueryRepository userQueryRepository;

    @Transactional
    public void createBlockedUser(Long userId, Long blockUserId) {
        User user = userQueryRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        User blockedUser = userQueryRepository.findById(blockUserId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        if (userId.equals(blockUserId)) {
            throw new CommonException(ErrorCode.CANNOT_BLOCK_MYSELF);
        }

        Optional<BlockedUser> checkBlockedUser = blockedUserQueryRepository.findByUserIdAndBlockedUserId(userId,
                blockUserId);
        if (checkBlockedUser.isPresent()) {
            throw new CommonException(ErrorCode.ALREADY_BLOCKED_USER);
        }

        BlockedUser createBlockedUser = BlockedUser.builder()
                .userId(user)
                .blockedUserId(blockedUser)
                .build();
        blockedUserQueryRepository.save(createBlockedUser);
    }

    // 차단한 유저 ID 리스트 조회
    public List<String> findBlockedUserList(User user) {
        return blockedUserQueryRepository.findAllByUserId(user)
                .stream()
                .map(blockedUser -> blockedUser.getId().toString())
                .toList();
    }
}
