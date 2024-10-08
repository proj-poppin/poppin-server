package com.poppin.poppinserver.modifyInfo.dto.response;

import com.poppin.poppinserver.modifyInfo.domain.ModifyInfo;
import com.poppin.poppinserver.popup.dto.popup.response.AdminPopupDto;
import com.poppin.poppinserver.popup.dto.popup.response.PopupDto;
import lombok.Builder;

import java.util.List;

@Builder
public record ModifyInfoDto(
        String id,
        String userId, // 작성자 id
        String userImageUrl, // 작성자 프로필 이미지
        String email, // 작성자 이메일
        String nickname, // 작성자 닉네임
        PopupDto popup, // 팝업
        String popupName,
        String createdAt, // 작성 일자
        String content, // 수정 요청 텍스트
        Boolean isExecuted, // 처리 여부
        String info, // 정보 처리 내용
        String agentName, // 담당 관리자
        List<String> images
) {
    public static ModifyInfoDto fromEntity(ModifyInfo modifyInfo, List<String> images) {

        PopupDto popupDto = null;
        if (modifyInfo != null) {
            popupDto = PopupDto.fromEntity(modifyInfo.getProxyPopup());
        }

        String agentName = null;
        if (modifyInfo.getOriginPopup().getAgent() != null) {
            agentName = modifyInfo.getOriginPopup().getAgent().getNickname();
        }

        String modifyInfoId = String.valueOf(modifyInfo.getId());
        String userId = String.valueOf(modifyInfo.getUserId());

        return ModifyInfoDto.builder()
                .id(modifyInfoId)
                .userId(userId)
                .userImageUrl(modifyInfo.getUserId().getProfileImageUrl())
                .email(modifyInfo.getUserId().getEmail())
                .nickname(modifyInfo.getUserId().getNickname())
                .popup(popupDto)
                .popupName(popupDto.name())
                .createdAt(modifyInfo.getCreatedAt().toString())
                .content(modifyInfo.getContent())
                .info(modifyInfo.getInfo())
                .agentName(agentName)
                .isExecuted(modifyInfo.getIsExecuted())
                .images(images)
                .build();
    }
}
