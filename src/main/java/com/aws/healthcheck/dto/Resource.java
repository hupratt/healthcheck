package com.aws.healthcheck.dto;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.aws.healthcheck.util.StringManipulation;

import lombok.Data;

@Data
public class Resource {

	@JsonProperty("Id")
	private String id;

	@JsonProperty("Description")
	private String description;

	public static List<String> fieldsToUseInTheExport() {
		return Arrays.stream(Resource.class.getDeclaredFields())
				.map(field -> StringManipulation.firstlettertoUpper(field.getName())).collect(Collectors.toList());
	}
}
