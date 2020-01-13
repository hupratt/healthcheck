package com.aws.healthcheck.util;

import java.nio.charset.Charset;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.client.RestTemplate;

@Configuration

public class RestTemplateConfig {
	@Value("${app.api.username}")
	private String username;
	@Value("${app.api.password}")
	private String password;

	public RestTemplateConfig(@Value("${comm.api.password}") String password,
			@Value("${comm.api.username}") String username) {
		this.password = password;
		this.username = username;
	}

	@Bean
	public RestTemplate restTemplate(final CloseableHttpClient httpClient) {
		return new RestTemplate(clientHttpRequestFactory(httpClient));
	}

	@Bean
	public HttpComponentsClientHttpRequestFactory clientHttpRequestFactory(final CloseableHttpClient httpClient) {
		final HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
		clientHttpRequestFactory.setHttpClient(httpClient);
		return clientHttpRequestFactory;
	}

	@Bean
	public TaskScheduler taskScheduler() {
		final ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
		scheduler.setThreadNamePrefix("poolScheduler");
		scheduler.setPoolSize(50);
		return scheduler;
	}

	@Bean
	public HttpHeaders createHeaders() {
		return new HttpHeaders() {
			private static final long serialVersionUID = 1992920934908826203L;

			{
				final String auth = username + ":" + password;
				final byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")));
				final String authHeader = "Basic " + new String(encodedAuth);
				set(HttpHeaders.AUTHORIZATION, authHeader);
				set(HttpHeaders.ACCEPT, "application/json");
			}
		};
	}

}
