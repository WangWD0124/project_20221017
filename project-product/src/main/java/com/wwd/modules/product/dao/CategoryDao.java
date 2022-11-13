package com.wwd.modules.product.dao;

import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.wwd.common.dao.BaseDao;
import com.wwd.modules.product.entity.CategoryEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;

/**
 * 商品三级分类
 *
 * @author wwd 1245436962@qq.com
 * @since 1.0.0 2022-10-14
 */
@Mapper
public interface CategoryDao extends BaseDao<CategoryEntity> {

}