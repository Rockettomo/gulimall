package com.wcj.gulimall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wcj.common.utils.PageUtils;
import com.wcj.gulimall.coupon.entity.CouponSpuRelationEntity;

import java.util.Map;

/**
 * 优惠券与产品关联
 *
 * @author wuchangjian
 * @email ${email}
 * @date 2023-03-05 09:45:48
 */
public interface CouponSpuRelationService extends IService<CouponSpuRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

