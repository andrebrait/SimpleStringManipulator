package com.brait.string_api;

import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@Data
@XmlRootElement
public class Response {
	private Long queryIndex;
	private String preferredName;
}
