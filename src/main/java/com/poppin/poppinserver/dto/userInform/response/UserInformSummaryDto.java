package com.poppin.poppinserver.dto.userInform.response;

import com.poppin.poppinserver.domain.Popup;
import com.poppin.poppinserver.domain.UserInform;
import com.poppin.poppinserver.dto.popup.response.PopupSummaryDto;
import com.poppin.poppinserver.type.EInformProgress;
import jakarta.validation.constraints.NotNull;
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
            UserInformSummaryDto userInformSummaryDto =
                    UserInformSummaryDto.builder()
                            .id(userInform.getId())
                            .progress(userInform.getProgress())
                            .popupName(userInform.getPopupId().getName())
                            .informerName(userInform.getInformerId().getNickname())
                            .informedAt(userInform.getInformedAt().toString())
                            .adminName(userInform.getAdminId().getNickname())
                            .executedAt(userInform.getExecutedAt().toString())
                            .build();

            dtoList.add(userInformSummaryDto);
        }

        return dtoList;
    }
}
