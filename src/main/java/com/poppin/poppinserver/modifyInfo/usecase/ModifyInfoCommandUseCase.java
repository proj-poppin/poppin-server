package com.poppin.poppinserver.modifyInfo.usecase;

public interface ModifyInfoCommandUseCase {
    // 관련된 모든 정보수정요청 삭제
    void deleteAllModifyInfo(Long userId);
}
