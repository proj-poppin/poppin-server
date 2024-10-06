package com.poppin.poppinserver.user.service;

import com.poppin.poppinserver.user.usecase.UserExampleUsecase;
import org.springframework.stereotype.Service;

@Service
public class UserExampleServiceImpl implements UserExampleUsecase {
    @Override
    public void test() {
        System.out.println("test");
    }
}
