package com.poppin.poppinserver.popup.dto.popup.response;

import com.poppin.poppinserver.alarm.dto.informAlarm.response.NoticeDto;
import lombok.Builder;

import java.util.List;

@Builder
public record BootstrapDto(
        List<PopupStoreDto> popularTop5PopupStores,
        List<PopupStoreDto> newlyOpenedPopupStores,
        List<PopupStoreDto> closingSoonPopupStores,
        List<PopupStoreDto> recommendedPopupStores,
        List<PopupStoreDto> interestedPopupStores,
        List<NoticeDto> notices
) {
}
