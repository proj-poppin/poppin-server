package com.poppin.poppinserver.user.dto.user.response;

import com.poppin.poppinserver.popup.dto.popup.response.PopupStoreDto;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record UserPreferenceUpdateResponseDto(
        UserPreferenceSettingDto userPreferenceSetting,
        List<PopupStoreDto> updatedRecommendedPopupStores
) {
    public static UserPreferenceUpdateResponseDto fromDtos(
            UserPreferenceSettingDto userPreferenceSetting,
            List<PopupStoreDto> updatedRecommendedPopupStores
    ) {
        return UserPreferenceUpdateResponseDto.builder()
                .userPreferenceSetting(userPreferenceSetting)
                .updatedRecommendedPopupStores(updatedRecommendedPopupStores)
                .build();
    }
}
