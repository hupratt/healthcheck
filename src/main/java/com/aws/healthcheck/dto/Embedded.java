package com.aws.healthcheck.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Embedded<E> {

	@JsonProperty("item")
	private List<E> item;
}
