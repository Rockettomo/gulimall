package com.wcj.gulimall.coupon.service.impl;

import com.wcj.common.to.MemberPrice;
import com.wcj.common.to.SkuReductionTo;
import com.wcj.gulimall.coupon.entity.MemberPriceEntity;
import com.wcj.gulimall.coupon.entity.SkuLadderEntity;
import com.wcj.gulimall.coupon.service.MemberPriceService;
import com.wcj.gulimall.coupon.service.SkuLadderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wcj.common.utils.PageUtils;
import com.wcj.common.utils.Query;

import com.wcj.gulimall.coupon.dao.SkuFullReductionDao;
import com.wcj.gulimall.coupon.entity.SkuFullReductionEntity;
import com.wcj.gulimall.coupon.service.SkuFullReductionService;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {

    @Autowired
    private SkuLadderService skuLadderService;
    @Autowired
    private MemberPriceService memberPriceService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SkuFullReductionEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 保存skuReductionTo中的信息
     *
     * @param skuReductionTo
     */
    @Override
    public void saveSkuReduction(SkuReductionTo skuReductionTo) {
        // 1.阶梯价格sms_sku_ladder
        SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
        BeanUtils.copyProperties(skuReductionTo, skuLadderEntity);
        if (skuReductionTo.getFullCount() > 0) {
            skuLadderService.save(skuLadderEntity);
        }
        // 2.满减sms_sku_full_reduction
        SkuFullReductionEntity skuFullReductionEntity = new SkuFullReductionEntity();
        BeanUtils.copyProperties(skuReductionTo, skuFullReductionEntity);

        if (skuReductionTo.getFullPrice().compareTo(new BigDecimal("0")) > 0) {
            this.save(skuFullReductionEntity);
        }
        // 3.会员价格sms_member_price
        List<MemberPrice> memberPrice = skuReductionTo.getMemberPrice();
        List<MemberPriceEntity> collect = memberPrice.stream().map(item -> {
                    MemberPriceEntity memberPriceEntity = new MemberPriceEntity();
                    memberPriceEntity.setSkuId(skuReductionTo.getSkuId());
                    memberPriceEntity.setMemberLevelId(item.getId());
                    memberPriceEntity.setMemberLevelName(item.getName());
                    memberPriceEntity.setMemberPrice(item.getPrice());
                    memberPriceEntity.setAddOther(1);

                    return memberPriceEntity;
                })
                .filter(item -> {
                    return item.getMemberPrice().compareTo(new BigDecimal("0")) > 0;
                })
                .collect(Collectors.toList());

        memberPriceService.saveBatch(collect);
    }

}