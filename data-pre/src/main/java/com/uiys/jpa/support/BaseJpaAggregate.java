package com.uiys.jpa.support;

import com.uiys.jpa.convert.DateLong2Instant;
import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Version;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.domain.AbstractAggregateRoot;


@Data
@EqualsAndHashCode(callSuper = true)
@MappedSuperclass
public class BaseJpaAggregate extends AbstractAggregateRoot<BaseJpaAggregate> {

	@Id
	@GenericGenerator(name = "uuid", strategy = "com.uiys.jpa.fgs.CustomerUUIDGenerator")
	@GeneratedValue(generator = "uuid")
	@Column(columnDefinition = "varchar(64) not null COMMENT '主键'")
	private String id;

	@Version
	@GenericGenerator(name = "version", strategy = "com.uiys.jpa.fgs.VersionGenerator")
	@GeneratedValue(generator = "version")
	@Column(columnDefinition = "int not null default 0 COMMENT '版本号'")
	private Integer version;

	@Convert(converter = DateLong2Instant.class)
	@Column(columnDefinition = "bigint not null COMMENT '创建时间'")
	private Instant createdAt;

	@Convert(converter = DateLong2Instant.class)
	@Column(columnDefinition = "bigint default null COMMENT '更新时间'")
	private Instant updatedAt;

	@PreUpdate
	public void update() {
		setUpdatedAt(Instant.now());
	}

	@PrePersist
	public void create() {
		setCreatedAt(Instant.now());
		setUpdatedAt(Instant.now());
	}

}


