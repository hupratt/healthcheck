package com.aws.healthcheck.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.aws.healthcheck.exceptions.ErrorResponse;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RestService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${comm.api.url}")
    private String localurl;

    public String getMessage() {
        String httpResponse = "";
        ErrorResponse errorResponse;
        try {
            httpResponse = restTemplate.getForObject(localurl + HealthService.URL_SUFFIX,
                    String.class);
            log.info("Success {}", httpResponse);

        } catch (HttpStatusCodeException e) {
            Integer errorCode = e.getRawStatusCode();
            String errorText = e.getStatusText();
            errorResponse = new ErrorResponse(errorCode, errorText);
            httpResponse = errorResponse.toString();
            log.debug("{} {}", errorResponse, e.getMessage());

        } catch (RuntimeException e) {
            log.error("{}", e.getMessage());
        }
        return httpResponse;
    }
}
