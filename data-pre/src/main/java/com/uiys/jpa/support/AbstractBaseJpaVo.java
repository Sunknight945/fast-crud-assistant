package com.uiys.jpa.support;

import java.util.Objects;
import lombok.Getter;
import lombok.ToString;


@ToString
@Getter
public class AbstractBaseJpaVo {
	private int version;
	private String id;
	private Long createdAt;
	private Long updatedAt;


	protected AbstractBaseJpaVo(){

	}

	protected AbstractBaseJpaVo(BaseJpaAggregate source) {
		this.setVersion(source.getVersion());
		this.setId(source.getId());
		this.setCreatedAt(source.getCreatedAt()
		  .toEpochMilli());
		this.setUpdatedAt(source.getUpdatedAt()
		  .toEpochMilli());
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setCreatedAt(Long createdAt) {
		this.createdAt = createdAt;
	}

	public void setUpdatedAt(Long updatedAt) {
		this.updatedAt = updatedAt;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {return true;}
		if (!(object instanceof AbstractBaseJpaVo)) {return false;}

		AbstractBaseJpaVo that = (AbstractBaseJpaVo) object;

		if (version != that.version) {return false;}
		if (!Objects.equals(id, that.id)) {return false;}
		if (!Objects.equals(createdAt, that.createdAt)) {return false;}
		return Objects.equals(updatedAt, that.updatedAt);
	}

	@Override
	public int hashCode() {
		int result = version;
		result = 31 * result + (id != null ? id.hashCode() : 0);
		result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
		result = 31 * result + (updatedAt != null ? updatedAt.hashCode() : 0);
		return result;
	}


}




