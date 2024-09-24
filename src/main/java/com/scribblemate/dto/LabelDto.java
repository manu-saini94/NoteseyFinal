package com.scribblemate.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LabelDto {

	private Integer id;

	private String labelName;

	@JsonProperty
	private boolean isImportant;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

}
