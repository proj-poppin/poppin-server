package com.poppin.poppinserver.user.service;

import com.poppin.poppinserver.core.exception.CommonException;
import com.poppin.poppinserver.core.exception.ErrorCode;
import com.poppin.poppinserver.user.domain.BlockedUser;
import com.poppin.poppinserver.user.domain.User;
import com.poppin.poppinserver.user.repository.BlockedUserRepository;
import com.poppin.poppinserver.user.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BlockUserService {
    private final BlockedUserRepository blockedUserRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createBlockedUser(Long userId, Long blockUserId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        User blockedUser = userRepository.findById(blockUserId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        if (userId.equals(blockUserId)) {
            throw new CommonException(ErrorCode.CANNOT_BLOCK_MYSELF);
        }

        Optional<BlockedUser> checkBlockedUser = blockedUserRepository.findByUserIdAndBlockedUserId(userId,
                blockUserId);
        if (checkBlockedUser.isPresent()) {
            throw new CommonException(ErrorCode.ALREADY_BLOCKED_USER);
        }

        BlockedUser createBlockedUser = BlockedUser.builder()
                .userId(user)
                .blockedUserId(blockedUser)
                .build();
        blockedUserRepository.save(createBlockedUser);
    }
}
