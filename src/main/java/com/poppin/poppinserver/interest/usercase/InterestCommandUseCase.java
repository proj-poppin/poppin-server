package com.poppin.poppinserver.interest.usercase;

public interface InterestCommandUseCase {
    void deleteAllInterestsByPopupId(Long popupId);
    void deleteExistByUserIdAndPopupId(Long userId, Long popupId); // 존재한다면 삭제
}
