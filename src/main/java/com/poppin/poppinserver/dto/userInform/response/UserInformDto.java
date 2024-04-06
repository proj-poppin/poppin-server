package com.poppin.poppinserver.dto.userInform.response;

import com.poppin.poppinserver.domain.Popup;
import com.poppin.poppinserver.domain.UserInform;
import com.poppin.poppinserver.dto.popup.response.PopupDto;
import com.poppin.poppinserver.type.EInformProgress;
import lombok.Builder;

import java.util.List;

@Builder
public record UserInformDto(
        Long id,
        Long informerId, // 제보자 id
        String informedAt,  // 제보 일자
        PopupDto popup, // 팝업
        String contactLink, // 정보를 접한 사이트 주소
        EInformProgress progress // 처리 상태(NOTEXECUTED | EXECUTING | EXECUTED)
) {
    public static UserInformDto fromEntity(UserInform userInform){
        PopupDto popupDto = PopupDto.fromEntity(userInform.getPopupId());

        return UserInformDto.builder()
                .id(userInform.getId())
                .informerId(userInform.getInformerId().getId())
                .informedAt(userInform.getInformedAt().toString())
                .popup(popupDto)
                .contactLink(userInform.getContactLink())
                .progress(userInform.getProgress())
                .build();
    }
}
