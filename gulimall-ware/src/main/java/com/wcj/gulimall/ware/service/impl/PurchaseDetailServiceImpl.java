package com.wcj.gulimall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wcj.gulimall.ware.entity.PurchaseEntity;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wcj.common.utils.PageUtils;
import com.wcj.common.utils.Query;

import com.wcj.gulimall.ware.dao.PurchaseDetailDao;
import com.wcj.gulimall.ware.entity.PurchaseDetailEntity;
import com.wcj.gulimall.ware.service.PurchaseDetailService;


@Service("purchaseDetailService")
public class PurchaseDetailServiceImpl extends ServiceImpl<PurchaseDetailDao, PurchaseDetailEntity> implements PurchaseDetailService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {

        /**
         * key: '华为',//检索关键字
         *    status: 0,//状态
         *    wareId: 1,//仓库id
         */

        LambdaQueryWrapper<PurchaseDetailEntity> wrapper = new LambdaQueryWrapper<>();
        String key = (String) params.get("key");
        if (StringUtils.isNotEmpty(key)) {
            wrapper.and((w) -> {
                w.eq(PurchaseDetailEntity::getId, key).or().eq(PurchaseDetailEntity::getSkuId, key);
            });
        }

        String status = (String) params.get("status");
        if (StringUtils.isNotEmpty(status)) {
            wrapper.eq(PurchaseDetailEntity::getStatus, status);
        }

        String wareId = (String) params.get("wareId");
        if (StringUtils.isNotEmpty(wareId)) {
            wrapper.eq(PurchaseDetailEntity::getWareId, wareId);
        }

        IPage<PurchaseDetailEntity> page = this.page(
                new Query<PurchaseDetailEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public List<PurchaseDetailEntity> listDetailByPurchaseId(Long id) {

        return this.list(new LambdaQueryWrapper<PurchaseDetailEntity>()
                .eq(PurchaseDetailEntity::getPurchaseId,id));
    }

}