package com.poppin.poppinserver.user.service;

import com.poppin.poppinserver.user.usecase.UserExampleUseCase;
import org.springframework.stereotype.Service;

@Service
public class UserExampleServiceImpl implements UserExampleUseCase {
    @Override
    public void test() {
        System.out.println("test");
    }
}
