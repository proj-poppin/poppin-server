package com.poppin.poppinserver.dto.modifyInfo.response;

import com.poppin.poppinserver.domain.ManagerInform;
import com.poppin.poppinserver.domain.ModifyInfo;
import com.poppin.poppinserver.dto.managerInform.response.ManagerInformSummaryDto;
import com.poppin.poppinserver.type.EInformProgress;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

@Builder
public record ModifyInfoSummaryDto(
        Long id,
        String popupName,
        String informerName,
        String informedAt,
        Boolean isExecuted
) {
    public static List<ModifyInfoSummaryDto> fromEntityList(List<ModifyInfo> modifyInfos){
        List<ModifyInfoSummaryDto> dtoList = new ArrayList<>();

        for(ModifyInfo modifyInfo : modifyInfos){

            ModifyInfoSummaryDto modifyInfoSummaryDto =
                    ModifyInfoSummaryDto.builder()
                            .id(modifyInfo.getId())
                            .popupName(modifyInfo.getPopupId().getName())
                            .informerName(modifyInfo.getUserId().getNickname())
                            .informedAt(modifyInfo.getCreatedAt().toString())
                            .isExecuted(modifyInfo.getIsExecuted())
                            .build();

            dtoList.add(modifyInfoSummaryDto);
        }

        return dtoList;
    }
}
