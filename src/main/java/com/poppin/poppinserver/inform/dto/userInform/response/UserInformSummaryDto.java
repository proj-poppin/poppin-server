package com.poppin.poppinserver.inform.dto.userInform.response;

import com.poppin.poppinserver.inform.domain.UserInform;
import com.poppin.poppinserver.core.type.EInformProgress;
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
    public static List<UserInformSummaryDto> fromEntityList(List<UserInform> userInforms) {
        List<UserInformSummaryDto> dtoList = new ArrayList<>();

        for (UserInform userInform : userInforms) {

            String executedAt = null;
            if (userInform.getExecutedAt() != null) {
                executedAt = userInform.getExecutedAt().toString();
            }

            String informerName = null;
            if (userInform.getInformerId() != null) {
                informerName = userInform.getInformerId().getNickname();
            }

            String adminName = null;
            if (userInform.getPopupId().getAgent() != null) {
                adminName = userInform.getPopupId().getAgent().getNickname();
            }

            UserInformSummaryDto userInformSummaryDto =
                    UserInformSummaryDto.builder()
                            .id(userInform.getId())
                            .progress(userInform.getProgress())
                            .popupName(userInform.getPopupId().getName())
                            .informerName(informerName)
                            .informedAt(userInform.getInformedAt().toString())
                            .executedAt(executedAt)
                            .adminName(adminName)
                            .build();

            dtoList.add(userInformSummaryDto);
        }

        return dtoList;
    }
}
