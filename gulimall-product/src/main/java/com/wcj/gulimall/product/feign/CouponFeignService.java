package com.wcj.gulimall.product.feign;

import com.wcj.common.to.SkuReductionTo;
import com.wcj.common.to.SpuBoundTo;
import com.wcj.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 远程调用使用JSON传输,远程地址写好,接收发送的数据模型中对应的属性相同即可(类似于API调用)
 */
@FeignClient("gulimall-coupon")
public interface CouponFeignService {

    @RequestMapping("coupon/spubounds/save")
    R saveSpuBounds(@RequestBody SpuBoundTo spuBoundTo);

    @RequestMapping("coupon/skufullreduction/saveinfo")
    R saveSkuReduction(@RequestBody SkuReductionTo skuReductionTo);
}
