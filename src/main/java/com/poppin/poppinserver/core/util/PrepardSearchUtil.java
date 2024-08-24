package com.poppin.poppinserver.core.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PrepardSearchUtil {
    public String prepareSearchText(String userInput) {
        String[] words = userInput.split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                sb.append(word);
                sb.append("*");
            }
        }
        return sb.toString().trim();
    }
}