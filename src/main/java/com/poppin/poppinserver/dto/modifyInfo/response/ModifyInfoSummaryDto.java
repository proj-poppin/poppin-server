package com.poppin.poppinserver.dto.modifyInfo.response;

import com.poppin.poppinserver.domain.ManagerInform;
import com.poppin.poppinserver.domain.ModifyInfo;
import com.poppin.poppinserver.dto.managerInform.response.ManagerInformSummaryDto;
import com.poppin.poppinserver.type.EInformProgress;

import java.util.ArrayList;
import java.util.List;

public record ModifyInfoSummaryDto(
        Long id,
        EInformProgress progress,
        String popupName,
        String informerName,
        String informedAt,
        String adminName,
        String executedAt
) {
    public static List<ModifyInfoSummaryDto> fromEntityList(List<ModifyInfo> modifyInfos){
        List<ModifyInfoSummaryDto> dtoList = new ArrayList<>();

        for(ModifyInfo modifyInfo : modifyInfos){

            ManagerInformSummaryDto modifyInfoSummaryDto =
                    ManagerInformSummaryDto.builder()
                            .id(modifyInfo.getId())
                            .popupName(modifyInfo.getPopupId().getName())
                            .informerName(modifyInfo.)
                            .informedAt(modifyInfo.getInformedAt().toString())
                            .adminName(modifyInfo.getAdminId().getNickname())
                            .executedAt(executedAt)
                            .build();

            dtoList.add(managerInformSummaryDto);
        }

        return dtoList;
    }
}
