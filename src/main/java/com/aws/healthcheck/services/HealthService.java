package com.aws.healthcheck.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.aws.healthcheck.util.RestTemplateConfig;

@Service
public class HealthService {

	public static final String URL_SUFFIX = "posts";
	private String apiUrl;

	private RestTemplate restTemplate;
	private RestTemplateConfig restTemplateConfig;

	public HealthService(RestTemplateConfig restTemplateConfig, RestTemplate restTemplate,
			@Value("${comm.api.url}") String apiUrl) {
		this.restTemplate = restTemplate;
		this.restTemplateConfig = restTemplateConfig;
		this.apiUrl = apiUrl;

	}

	public Response<Resource> getHttpCall() {
		final HttpEntity<String> request = new HttpEntity<String>(restTemplateConfig.createHeaders());

		final ResponseEntity<Response<Resource>> response = restTemplate.exchange(
				apiUrl + URL_SUFFIX, HttpMethod.GET, request,
				new ParameterizedTypeReference<Response<Resource>>() {
				});

		return response.getBody();

	}

}
