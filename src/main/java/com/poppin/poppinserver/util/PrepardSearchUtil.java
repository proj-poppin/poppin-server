package com.poppin.poppinserver.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PrepardSearchUtil {
    public String prepareSearchText(String userInput) {
        String[] words = userInput.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            sb.append("*");
            sb.append(word);
            sb.append("* ");
        }
        return sb.toString().trim();
    }
}
