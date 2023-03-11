package com.wcj.gulimall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wcj.common.utils.PageUtils;
import com.wcj.gulimall.order.entity.OrderOperateHistoryEntity;

import java.util.Map;

/**
 * 订单操作历史记录
 *
 * @author wuchangjian
 * @email ${email}
 * @date 2023-03-05 10:04:52
 */
public interface OrderOperateHistoryService extends IService<OrderOperateHistoryEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

