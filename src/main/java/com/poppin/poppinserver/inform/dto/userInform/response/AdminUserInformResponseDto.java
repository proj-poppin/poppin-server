package com.poppin.poppinserver.inform.dto.userInform.response;

import com.poppin.poppinserver.core.type.EInformProgress;
import com.poppin.poppinserver.inform.domain.UserInform;
import com.poppin.poppinserver.popup.dto.popup.response.AdminPopupDto;
import lombok.Builder;

@Builder
public record AdminUserInformResponseDto(
        Long id,
        Long informerId, // 제보자 id
        String informedAt,  // 제보 일자
        AdminPopupDto popup, // 팝업
        String contactLink, // 정보를 접한 사이트 주소
        EInformProgress progress // 처리 상태(NOTEXECUTED | EXECUTING | EXECUTED)
) {
    public static AdminUserInformResponseDto fromEntity(UserInform userInform) {
        AdminPopupDto adminPopupDto = AdminPopupDto.fromEntity(userInform.getPopupId());

        Long informerId = null;
        if (userInform.getInformerId() != null) {
            informerId = userInform.getId();
        }

        return AdminUserInformResponseDto.builder()
                .id(userInform.getId())
                .informerId(informerId)
                .informedAt(userInform.getInformedAt().toString())
                .popup(adminPopupDto)
                .contactLink(userInform.getContactLink())
                .progress(userInform.getProgress())
                .build();
    }
}
