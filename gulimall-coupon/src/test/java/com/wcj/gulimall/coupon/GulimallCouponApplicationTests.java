package com.wcj.gulimall.coupon;

import com.wcj.common.utils.PageUtils;
import com.wcj.gulimall.coupon.dao.CouponDao;
import com.wcj.gulimall.coupon.entity.CouponEntity;
import com.wcj.gulimall.coupon.service.CouponService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class GulimallCouponApplicationTests {

    @Autowired
    private CouponDao couponDao;

    @Test
    void contextLoads() {
        List<CouponEntity> couponEntities = couponDao.selectList(null);
        System.out.println(couponEntities);
    }

}
