package com.poppin.poppinserver.dto.modifyInfo.response;

import com.poppin.poppinserver.domain.*;
import com.poppin.poppinserver.dto.popup.response.PopupDto;
import lombok.Builder;

import java.util.List;

@Builder
public record ModifyInfoDto(
        Long id,
        Long userId, // 작성자 id
        PopupDto popup, // 팝업
        String createdAt, // 작성 일자
        String content, // 수정 요청 텍스트
        Boolean isExecuted // 처리 여부
) {
    public static ModifyInfoDto fromEntity(ModifyInfo modifyInfo){
        PopupDto popupDto = PopupDto.fromEntity(modifyInfo.getPopupId());

        return ModifyInfoDto.builder()
                .id(modifyInfo.getId())
                .userId(modifyInfo.getId())
                .popup(popupDto)
                .createdAt(modifyInfo.getCreatedAt().toString())
                .content(modifyInfo.getContent())
                .isExecuted(modifyInfo.getIsExecuted())
                .build();
    }
}
