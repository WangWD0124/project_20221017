package com.wwd.modules.product.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 属性分组
 *
 * @author wwd 1245436962@qq.com
 * @since 1.0.0 2022-10-14
 */
@Data
@TableName("pms_attr_group")
public class AttrGroupEntity {

    /**
     * 分组id
     */
    @TableId(type = IdType.AUTO)
	private Long attrGroupId;
    /**
     * 组名
     */
	private String attrGroupName;
    /**
     * 排序
     */
	private Integer sort;
    /**
     * 描述
     */
	private String descript;
    /**
     * 组图标
     */
	private String icon;
    /**
     * 所属分类id
     */
	private Long catelogId;
}