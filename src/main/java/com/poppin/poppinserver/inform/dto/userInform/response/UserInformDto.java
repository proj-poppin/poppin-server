package com.poppin.poppinserver.inform.dto.userInform.response;

import com.poppin.poppinserver.core.type.EInformProgress;
import com.poppin.poppinserver.inform.domain.UserInform;
import com.poppin.poppinserver.popup.dto.popup.response.AdminPopupDto;
import com.poppin.poppinserver.popup.dto.popup.response.PopupDto;
import lombok.Builder;

@Builder
public record UserInformDto(
        String id,
        String informerId, // 제보자 id
        String informedAt,  // 제보 일자
        PopupDto popup, // 팝업
        String contactLink, // 정보를 접한 사이트 주소
        EInformProgress progress // 처리 상태(NOTEXECUTED | EXECUTING | EXECUTED)
) {
    public static UserInformDto fromEntity(UserInform userInform) {
        PopupDto popupDto = PopupDto.fromEntity(userInform.getPopupId());

        Long informerId = null;
        if (userInform.getInformerId() != null) {
            informerId = userInform.getId();
        }

        return UserInformDto.builder()
                .id(String.valueOf(userInform.getId()))
                .informerId(String.valueOf(informerId))
                .informedAt(userInform.getInformedAt().toString())
                .popup(popupDto)
                .contactLink(userInform.getContactLink())
                .progress(userInform.getProgress())
                .build();
    }
}
