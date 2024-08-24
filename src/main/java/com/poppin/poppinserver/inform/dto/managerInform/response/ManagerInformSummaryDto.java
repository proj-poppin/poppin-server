package com.poppin.poppinserver.inform.dto.managerInform.response;

import com.poppin.poppinserver.inform.domain.ManagerInform;
import com.poppin.poppinserver.core.type.EInformProgress;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

@Builder
public record ManagerInformSummaryDto(
      Long id,
      EInformProgress progress,
      String popupName,
      String informerName,
      String informedAt,
      String adminName,
      String executedAt
) {
    public static List<ManagerInformSummaryDto> fromEntityList(List<ManagerInform> managerInforms){
        List<ManagerInformSummaryDto> dtoList = new ArrayList<>();

        for(ManagerInform managerInform : managerInforms){

            String executedAt = null;
            if(managerInform.getExecutedAt() != null){
                executedAt = managerInform.getExecutedAt().toString();
            }

            String informerName = null;
            if (managerInform.getInformerId() != null){
                informerName = managerInform.getInformerId().getNickname();
            }

            String adminName = null;
            if (managerInform.getPopupId().getAgent() != null) {
                adminName = managerInform.getPopupId().getAgent().getNickname();
            }

            ManagerInformSummaryDto managerInformSummaryDto =
                    ManagerInformSummaryDto.builder()
                            .id(managerInform.getId())
                            .progress(managerInform.getProgress())
                            .popupName(managerInform.getPopupId().getName())
                            .informerName(informerName)
                            .informedAt(managerInform.getInformedAt().toString())
                            .executedAt(executedAt)
                            .adminName(adminName)
                            .build();

            dtoList.add(managerInformSummaryDto);
        }

        return dtoList;
    }
}

