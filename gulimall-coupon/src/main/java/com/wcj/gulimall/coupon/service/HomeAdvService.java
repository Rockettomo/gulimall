package com.wcj.gulimall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wcj.common.utils.PageUtils;
import com.wcj.gulimall.coupon.entity.HomeAdvEntity;

import java.util.Map;

/**
 * 首页轮播广告
 *
 * @author wuchangjian
 * @email ${email}
 * @date 2023-03-05 09:45:48
 */
public interface HomeAdvService extends IService<HomeAdvEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

