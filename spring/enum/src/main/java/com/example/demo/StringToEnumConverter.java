package com.example.demo;

import org.springframework.core.convert.converter.Converter;

public class StringToEnumConverter implements Converter<String, RequestPostType> {
	@Override
	public RequestPostType convert(String source) {
		try {
			return RequestPostType.valueOf(source.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new CustomException(e.getMessage());
		}
	}
}