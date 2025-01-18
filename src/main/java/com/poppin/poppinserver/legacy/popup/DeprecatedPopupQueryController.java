//package com.poppin.poppinserver.legacy.popup;
//
//import com.poppin.poppinserver.admin.service.AdminPopupService;
//import com.poppin.poppinserver.popup.service.ListingPopupService;
//import com.poppin.poppinserver.popup.service.PopupService;
//import com.poppin.poppinserver.popup.service.SearchPopupService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@Slf4j
//@RequiredArgsConstructor
//@RequestMapping("/api/v1/popup")
//public class DeprecatedPopupQueryController {
//    private final PopupService popupService;
//    private final ListingPopupService listingPopupService;
//    private final SearchPopupService searchPopupService;
//
//    private final AdminPopupService adminPopupService;
//
//
//    @GetMapping("/search/base") // 로그인 팝업 베이스 검색
//    public ResponseDto<?> readBaseList(@RequestParam("searchName") String searchName,
//                                       @RequestParam("page") int page,
//                                       @RequestParam("size") int size,
//                                       @UserId Long userId) {
//        return ResponseDto.ok(searchPopupService.readBaseList(searchName, page, size, userId));
//    }
//
//    @GetMapping("/guest/search") // 비로그인 팝업 검색
//    public ResponseDto<?> readGuestSearchList(@RequestParam("searchName") String searchName,
//                                              @RequestParam("filteringThreeCategories") String filteringThreeCategories,
//                                              @RequestParam("filteringFourteenCategories") String filteringFourteenCategories,
//                                              @RequestParam("operationStatus") EOperationStatus oper,
//                                              @RequestParam("order") EPopupSort order,
//                                              @RequestParam("page") int page,
//                                              @RequestParam("size") int size) {
//        return ResponseDto.ok(searchPopupService.readGuestSearchingList(searchName, filteringThreeCategories,
//                filteringFourteenCategories, oper, order, page, size));
//    }
//
//    @GetMapping("/guest/search/base") // 비로그인 팝업 베이스 검색
//    public ResponseDto<?> readGuestBaseList(@RequestParam("searchName") String searchName,
//                                            @RequestParam("page") int page,
//                                            @RequestParam("size") int size) {
//        return ResponseDto.ok(searchPopupService.readGuestBaseList(searchName, page, size));
//    }
//
//    @GetMapping("/guest/detail")
//    public ResponseDto<PopupGuestDetailDto> readGuestDetail(@RequestParam("popupId") String popupId) {
//
//        return ResponseDto.ok(popupService.readGuestDetail(popupId));
//    } // 비로그인 상세조회
//
//    @GetMapping("/detail")
//    public ResponseDto<?> readDetail(@RequestParam("popupId") String popupId, @UserId Long userId) {
//
//        return ResponseDto.ok(popupService.readDetail(popupId, userId));
//    } // 로그인 상세조회
//}
