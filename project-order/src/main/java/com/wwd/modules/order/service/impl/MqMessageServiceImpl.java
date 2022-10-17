package com.wwd.modules.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wwd.common.service.impl.CrudServiceImpl;
import com.wwd.modules.order.dao.MqMessageDao;
import com.wwd.modules.order.dto.MqMessageDTO;
import com.wwd.modules.order.entity.MqMessageEntity;
import com.wwd.modules.order.service.MqMessageService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 
 *
 * @author wwd 1245436962@qq.com
 * @since 1.0.0 2022-10-14
 */
@Service
public class MqMessageServiceImpl extends CrudServiceImpl<MqMessageDao, MqMessageEntity, MqMessageDTO> implements MqMessageService {

    @Override
    public QueryWrapper<MqMessageEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");

        QueryWrapper<MqMessageEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);

        return wrapper;
    }


}