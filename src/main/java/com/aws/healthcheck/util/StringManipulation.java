package com.aws.healthcheck.util;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.aws.healthcheck.dto.Resource;
import com.aws.healthcheck.dto.Response;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StringManipulation {

    public static String firstlettertoUpper(String text) {
        String allExceptFirstLetter = text.substring(1);
        return Character.toUpperCase(text.charAt(0)) + allExceptFirstLetter;
    }

    public static String responseToString(Response<Resource> res) {
        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = new String();
        try {
            return mapper.writeValueAsString(res);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return jsonInString;
    }
}
