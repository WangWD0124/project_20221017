/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.wwd.dao;

import com.wwd.common.dao.BaseDao;
import com.wwd.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户
 * 
 * @author Mark sunlightcs@gmail.com
 */
@Mapper
public interface UserDao extends BaseDao<UserEntity> {
    UserEntity getUserByMobile(String mobile);

    UserEntity getUserByUserId(Long userId);
}
