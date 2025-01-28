package com.poppin.poppinserver.user.dto.user.response;

import com.poppin.poppinserver.popup.domain.PreferedPopup;
import com.poppin.poppinserver.popup.domain.TastePopup;
import com.poppin.poppinserver.popup.domain.WhoWithPopup;
import com.poppin.poppinserver.popup.dto.popup.response.PreferredDto;
import com.poppin.poppinserver.popup.dto.popup.response.TasteDto;
import com.poppin.poppinserver.popup.dto.popup.response.WhoWithDto;
import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record UserPreferenceSettingDto(
        PreferredDto preferencePopupStore,
        TasteDto preferenceCategory,
        WhoWithDto preferenceCompanion
) {
    public static UserPreferenceSettingDto fromEntity(
            PreferedPopup preferedPopup,
            TastePopup tastePopup,
            WhoWithPopup whoWithPopup
    ) {
        PreferredDto preferredDto = PreferredDto.fromEntity(preferedPopup);
        TasteDto tasteDto = TasteDto.fromEntity(tastePopup);
        WhoWithDto whoWithDto = WhoWithDto.fromEntity(whoWithPopup);

        return UserPreferenceSettingDto.builder()
                .preferencePopupStore(preferredDto)
                .preferenceCategory(tasteDto)
                .preferenceCompanion(whoWithDto)
                .build();
    }
}
