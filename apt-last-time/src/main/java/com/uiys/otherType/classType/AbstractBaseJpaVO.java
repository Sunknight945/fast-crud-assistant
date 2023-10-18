package com.uiys.otherType.classType;//package com.uiys.otherType.classType;
//
//import io.swagger.v3.oas.annotations.media.Schema;
//import lombok.Data;
//
///**
// * @author uiys
// * @date 2023/10/13
// */
//@Data
//public class AbstractBaseJpaVO {
//
//	@Schema(title = "主键")
//	private Long id;
//
//	@Schema(title = "版本")
//	private int version;
//
//	@Schema(title = "创建时间")
//	private Long createdAt;
//
//	@Schema(title = "修改时间")
//	private Long updatedAt;
//
//	protected AbstractBaseJpaVO() {
//	}
//
//	protected AbstractBaseJpaVO(BaseJpaAggregate source) {
//		this.setId(source.getId());
//		this.setVersion(source.getVersion());
//		this.setCreatedAt(source.getCreatedAt().toEpochMilli());
//		this.setUpdatedAt(source.getUpdatedAt().toEpochMilli());
//	}
//
//}
//
//
