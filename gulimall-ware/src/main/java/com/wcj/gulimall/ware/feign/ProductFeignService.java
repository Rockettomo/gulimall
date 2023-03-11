package com.wcj.gulimall.ware.feign;

import com.wcj.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("gulimall-gateway")
public interface ProductFeignService {

    /**
     * 可以发给网关，也可以直接发给后台服务
     * @param skuId
     * @return
     */
    @RequestMapping("/api/product/skuinfo/info/{skuId}")
    R info(@PathVariable("skuId") Long skuId);

}
