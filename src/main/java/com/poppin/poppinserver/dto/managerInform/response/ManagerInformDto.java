package com.poppin.poppinserver.dto.managerInform.response;

import com.poppin.poppinserver.domain.ManagerInform;
import com.poppin.poppinserver.domain.UserInform;
import com.poppin.poppinserver.dto.popup.response.PopupDto;
import com.poppin.poppinserver.dto.userInform.response.UserInformDto;
import com.poppin.poppinserver.type.EInformProgress;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record ManagerInformDto(
        Long id,
        Long informerId, // 제보자 id
        String informedAt,  // 제보 일자
        String affiliation, // 소속
        String informerEmail, // 담당자 이메일
        PopupDto popup, // 팝업
        EInformProgress progress // 처리 상태(NOTEXECUTED | EXECUTING | EXECUTED)
) {
    public static ManagerInformDto fromEntity(ManagerInform managerInform){
        PopupDto popupDto = PopupDto.fromEntity(managerInform.getPopupId());

        return ManagerInformDto.builder()
                .id(managerInform.getId())
                .informerId(managerInform.getInformerId().getId())
                .informedAt(managerInform.getInformedAt().toString())
                .popup(popupDto)
                .progress(managerInform.getProgress())
                .affiliation(managerInform.getAffiliation())
                .informerEmail(managerInform.getInformerEmail())
                .build();
    }
}
