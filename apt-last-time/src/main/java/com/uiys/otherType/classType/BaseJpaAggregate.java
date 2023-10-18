package com.uiys.otherType.classType;//package com.uiys.otherType.classType;
//
//import java.time.Instant;
//import javax.persistence.Column;
//import javax.persistence.Convert;
//import javax.persistence.GeneratedValue;
//import javax.persistence.GenerationType;
//import javax.persistence.Id;
//import javax.persistence.MappedSuperclass;
//import javax.persistence.PrePersist;
//import javax.persistence.PreUpdate;
//import lombok.Data;
//import lombok.EqualsAndHashCode;
//import org.springframework.data.domain.AbstractAggregateRoot;
//
///**
// * @author uiys
// * @date 2023/10/13
// */
//@Data
//@EqualsAndHashCode(callSuper = true)
//@MappedSuperclass
//public abstract class BaseJpaAggregate extends AbstractAggregateRoot<BaseJpaAggregate> {
//
//
//	@Id
//	@Column(name = "id")
//	@GeneratedValue(strategy = GenerationType.AUTO)
//	private Long id;
//
//	@Column(name = "version")
//	private int version;
//
//	@Column(name = "created_at")
//	@Convert(converter = DateCovertToLong.class)
//	private Instant createdAt;
//
//	@Column(name = "updated_at")
//	@Convert(converter = DateCovertToLong.class)
//	private Instant updatedAt;
//
//
//	@PrePersist
//	public void preCreate() {
//		setCreatedAt(Instant.now());
//		setUpdatedAt(Instant.now());
//	}
//
//	@PreUpdate
//	public void preUpdate() {
//		setUpdatedAt(Instant.now());
//	}
//
//}
//
//
