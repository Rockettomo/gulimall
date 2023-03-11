package com.wcj.gulimall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wcj.common.utils.PageUtils;
import com.wcj.gulimall.order.entity.MqMessageEntity;

import java.util.Map;

/**
 * 
 *
 * @author wuchangjian
 * @email ${email}
 * @date 2023-03-05 10:04:52
 */
public interface MqMessageService extends IService<MqMessageEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

