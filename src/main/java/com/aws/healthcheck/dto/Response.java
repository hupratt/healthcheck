package com.aws.healthcheck.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Response<E> {

	@JsonProperty("_embedded")
	private Embedded<E> embedded;

}
