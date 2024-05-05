package com.poppin.poppinserver.dto.userInform.response;

import com.poppin.poppinserver.domain.UserInform;
import com.poppin.poppinserver.type.EInformProgress;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

@Builder
public record UserInformSummaryDto(
   Long id,
   EInformProgress progress,
   String popupName,
   String informerName,
   String informedAt,
   String adminName,
   String executedAt
) {
    public static List<UserInformSummaryDto> fromEntityList(List<UserInform> userInforms){
        List<UserInformSummaryDto> dtoList = new ArrayList<>();

        for(UserInform userInform : userInforms){

            String executedAt = null;
            if(userInform.getExecutedAt() != null){
                executedAt = userInform.getExecutedAt().toString();
            }

            UserInformSummaryDto userInformSummaryDto =
                    UserInformSummaryDto.builder()
                            .id(userInform.getId())
                            .progress(userInform.getProgress())
                            .popupName(userInform.getPopupId().getName())
                            .informerName(userInform.getInformerId().getNickname())
                            .informedAt(userInform.getInformedAt().toString())
                            .executedAt(executedAt)
                            .build();

            dtoList.add(userInformSummaryDto);
        }

        return dtoList;
    }
}
