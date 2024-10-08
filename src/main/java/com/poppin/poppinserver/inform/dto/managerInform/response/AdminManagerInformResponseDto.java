package com.poppin.poppinserver.inform.dto.managerInform.response;

import com.poppin.poppinserver.core.type.EInformProgress;
import com.poppin.poppinserver.inform.domain.ManagerInform;
import com.poppin.poppinserver.popup.dto.popup.response.AdminPopupDto;
import lombok.Builder;

@Builder
public record AdminManagerInformResponseDto(
        Long id,
        Long informerId, // 제보자 id
        String informedAt,  // 제보 일자
        String affiliation, // 소속
        String informerEmail, // 담당자 이메일
        AdminPopupDto popup, // 팝업
        EInformProgress progress // 처리 상태(NOTEXECUTED | EXECUTING | EXECUTED)
) {
    public static AdminManagerInformResponseDto fromEntity(ManagerInform managerInform) {
        AdminPopupDto adminPopupDto = AdminPopupDto.fromEntity(managerInform.getPopupId());

        Long informerId = null;
        if (managerInform.getInformerId() != null) {
            informerId = managerInform.getId();
        }

        return AdminManagerInformResponseDto.builder()
                .id(managerInform.getId())
                .informerId(informerId)
                .informedAt(managerInform.getInformedAt().toString())
                .popup(adminPopupDto)
                .progress(managerInform.getProgress())
                .affiliation(managerInform.getAffiliation())
                .informerEmail(managerInform.getInformerEmail())
                .build();
    }
}
