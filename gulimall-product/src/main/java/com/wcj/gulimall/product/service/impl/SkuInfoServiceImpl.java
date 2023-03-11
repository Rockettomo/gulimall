package com.wcj.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.Objects;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wcj.common.utils.PageUtils;
import com.wcj.common.utils.Query;

import com.wcj.gulimall.product.dao.SkuInfoDao;
import com.wcj.gulimall.product.entity.SkuInfoEntity;
import com.wcj.gulimall.product.service.SkuInfoService;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuInfo(SkuInfoEntity skuInfoEntity) {
        baseMapper.insert(skuInfoEntity);
    }

    /**
     * 检索sku
     * @param params
     * @return
     */
    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {

        LambdaQueryWrapper<SkuInfoEntity> wrapper = new LambdaQueryWrapper<>();
        String key = (String)params.get("key");
        if(StringUtils.isNotEmpty(key)){
            wrapper.and((w)->{
                w.eq(SkuInfoEntity::getSkuId,key).or().like(SkuInfoEntity::getSkuName,key);
            });
        }
        String catelogId = (String)params.get("catelogId");
        if(!Objects.equals(catelogId, "0") && StringUtils.isNotEmpty(catelogId)){
            wrapper.eq(SkuInfoEntity::getCatalogId,catelogId);
        }
        String brandId = (String)params.get("brandId");
        if(!Objects.equals(brandId, "0") && StringUtils.isNotEmpty(brandId)){
            wrapper.eq(SkuInfoEntity::getBrandId,brandId);
        }
        String min = (String)params.get("min");
        if(!Objects.equals(min, "0") && StringUtils.isNotEmpty(min)){
            wrapper.ge(SkuInfoEntity::getPrice,min);
        }
        String max = (String)params.get("max");
        if(!Objects.equals(max, "0") && StringUtils.isNotEmpty(max)){
            wrapper.le(SkuInfoEntity::getPrice,max);
        }

        IPage<SkuInfoEntity> page = this.page(new Query<SkuInfoEntity>().getPage(params),wrapper);

        return new PageUtils(page);

    }

}