package com.wcj.gulimall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wcj.common.utils.PageUtils;
import com.wcj.gulimall.order.entity.PaymentInfoEntity;

import java.util.Map;

/**
 * 支付信息表
 *
 * @author wuchangjian
 * @email ${email}
 * @date 2023-03-05 10:04:52
 */
public interface PaymentInfoService extends IService<PaymentInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

