package com.wcj.gulimall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wcj.common.utils.R;
import com.wcj.gulimall.ware.feign.ProductFeignService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wcj.common.utils.PageUtils;
import com.wcj.common.utils.Query;

import com.wcj.gulimall.ware.dao.WareSkuDao;
import com.wcj.gulimall.ware.entity.WareSkuEntity;
import com.wcj.gulimall.ware.service.WareSkuService;
import org.springframework.transaction.annotation.Transactional;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    private ProductFeignService productFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        LambdaQueryWrapper<WareSkuEntity> wrapper = new LambdaQueryWrapper<>();
        String skuId = (String) params.get("skuId");
        if (StringUtils.isNotEmpty(skuId)) {
            wrapper.eq(WareSkuEntity::getId, skuId);
        }
        String wareId = (String) params.get("wareId");
        if (StringUtils.isNotEmpty(skuId)) {
            wrapper.eq(WareSkuEntity::getWareId, wareId);
        }

        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    /**
     * 添加库存(给哪个仓,哪个商品,添加多少库存)
     *
     * @param skuId
     * @param wareId
     * @param skuNum
     */
    @Transactional
    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        // 1.判断,如果没有库存记录就是新增操作
        List<WareSkuEntity> wareSkuEntities = baseMapper.selectList(
                new LambdaQueryWrapper<WareSkuEntity>()
                        .eq(WareSkuEntity::getSkuId, skuId)
                        .eq(WareSkuEntity::getWareId, wareId)
        );
        if (wareSkuEntities.size() == 0) {
            WareSkuEntity skuEntity = new WareSkuEntity();
            skuEntity.setSkuId(skuId);
            skuEntity.setStock(skuNum);
            skuEntity.setWareId(wareId);
            skuEntity.setStockLocked(0);
            // 远程查询skuName
            //TODO 让异常出现之后不回滚
            try{
                R info = productFeignService.info(skuId);
                if (info.getCode() == 0) {
                    Map<String,String> data =(Map<String,String>) info.get("skuInfo");
                    skuEntity.setSkuName((String) data.get("skuName"));
                }
            }catch (Exception e){
                e.printStackTrace();
            }


            baseMapper.insert(skuEntity);
        } else {
            baseMapper.addStock(skuId, wareId, skuNum);
        }

    }

}