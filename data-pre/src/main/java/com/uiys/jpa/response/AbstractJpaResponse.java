package com.uiys.jpa.response;

import com.uiys.jpa.controller.Response;
import lombok.Data;


@Data
public class AbstractJpaResponse implements Response {

	private String id;

	private Integer version;

	private Long createdAt;

	private Long updatedAt;

	public AbstractJpaResponse() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public Long getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Long createdAt) {
		this.createdAt = createdAt;
	}

	public Long getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Long updatedAt) {
		this.updatedAt = updatedAt;
	}
}


