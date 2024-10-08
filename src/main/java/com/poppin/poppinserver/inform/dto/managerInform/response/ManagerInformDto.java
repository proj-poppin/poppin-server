package com.poppin.poppinserver.inform.dto.managerInform.response;

import com.poppin.poppinserver.core.type.EInformProgress;
import com.poppin.poppinserver.inform.domain.ManagerInform;
import com.poppin.poppinserver.popup.dto.popup.response.AdminPopupDto;
import com.poppin.poppinserver.popup.dto.popup.response.PopupDto;
import lombok.Builder;

@Builder
public record ManagerInformDto(
        String id,
        String informerId, // 제보자 id
        String informedAt,  // 제보 일자
        String affiliation, // 소속
        String informerEmail, // 담당자 이메일
        PopupDto popup, // 팝업
        EInformProgress progress // 처리 상태(NOTEXECUTED | EXECUTING | EXECUTED)
) {
    public static ManagerInformDto fromEntity(ManagerInform managerInform) {
        PopupDto popupDto = PopupDto.fromEntity(managerInform.getPopupId());

        Long informerId = null;
        if (managerInform.getInformerId() != null) {
            informerId = managerInform.getId();
        }

        return ManagerInformDto.builder()
                .id(String.valueOf(managerInform.getId()))
                .informerId(String.valueOf(informerId))
                .informedAt(managerInform.getInformedAt().toString())
                .popup(popupDto)
                .progress(managerInform.getProgress())
                .affiliation(managerInform.getAffiliation())
                .informerEmail(managerInform.getInformerEmail())
                .build();
    }
}
