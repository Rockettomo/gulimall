package com.wcj.gulimall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wcj.common.constant.WareConstant;
import com.wcj.gulimall.ware.entity.PurchaseDetailEntity;
import com.wcj.gulimall.ware.entity.WareSkuEntity;
import com.wcj.gulimall.ware.service.PurchaseDetailService;
import com.wcj.gulimall.ware.service.WareSkuService;
import com.wcj.gulimall.ware.vo.MergeVo;
import com.wcj.gulimall.ware.vo.PurchaseDoneVo;
import com.wcj.gulimall.ware.vo.PurchaseItemDoneVo;
import org.apache.commons.lang.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wcj.common.utils.PageUtils;
import com.wcj.common.utils.Query;

import com.wcj.gulimall.ware.dao.PurchaseDao;
import com.wcj.gulimall.ware.entity.PurchaseEntity;
import com.wcj.gulimall.ware.service.PurchaseService;
import org.springframework.transaction.annotation.Transactional;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

    @Autowired
    private PurchaseDetailService purchaseDetailService;
    @Autowired
    private WareSkuService wareSkuService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageUnreceivePurchase(Map<String, Object> params) {


        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new LambdaQueryWrapper<PurchaseEntity>()
                        .eq(PurchaseEntity::getStatus, 0)
                        .or()
                        .eq(PurchaseEntity::getWareId, 1)
        );

        return new PageUtils(page);

    }

    @Transactional
    @Override
    public void mergePurchase(MergeVo mergeVo) {
        Long purchaseId = mergeVo.getPurchaseId();
        if (purchaseId == null) {
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setStatus(WareConstant.PurchaseStatus.GREATED.getCode());
            purchaseEntity.setCreateTime(new Date());
            purchaseEntity.setUpdateTime(new Date());
            this.save(purchaseEntity);

            purchaseId = purchaseEntity.getId();
        }

        //TODO 确认采购单状态是0,1才可以合并

        Long finalPurchaseId = purchaseId;
        List<Long> items = mergeVo.getItems();
        List<PurchaseDetailEntity> collect = items.stream().map(i -> {
            PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
            detailEntity.setId(i);
            detailEntity.setPurchaseId(finalPurchaseId);
            detailEntity.setStatus(WareConstant.PurchaseDetailStatus.ASSINED.getCode());

            return detailEntity;
        }).collect(Collectors.toList());

        purchaseDetailService.updateBatchById(collect);

        // 更新修改时间
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(purchaseId);
        purchaseEntity.setUpdateTime(new Date());

        this.updateById(purchaseEntity);
    }

    /**
     * 领取采购单
     *
     * @param ids
     */
    @Override
    public void recevied(List<Long> ids) {
        //1. 确认当前采购单是新建或者已分配
        List<PurchaseEntity> collect = ids.stream().map(id -> {
                    return this.getById(id);
                }).filter(item -> {
                    return item.getStatus() == WareConstant.PurchaseStatus.GREATED.getCode()
                            || item.getStatus() == WareConstant.PurchaseStatus.ASSINED.getCode();
                }).map(item -> {
                    item.setStatus(WareConstant.PurchaseStatus.RECEIVE.getCode());
                    item.setUpdateTime(new Date());
                    return item;
                })
                .collect(Collectors.toList());

        //2. 改变采购单的状态
        this.updateBatchById(collect);
        //3. 改变采购项的状态
        collect.forEach(item -> {
            List<PurchaseDetailEntity> purchaseDetailEntities = purchaseDetailService.listDetailByPurchaseId(item.getId());
            List<PurchaseDetailEntity> collect1 = purchaseDetailEntities.stream().map(purchaseDetailEntity -> {
                PurchaseDetailEntity purchaseDetailEntity1 = new PurchaseDetailEntity();
                purchaseDetailEntity1.setId(purchaseDetailEntity.getId());

                purchaseDetailEntity1.setStatus(WareConstant.PurchaseDetailStatus.BUYING.getCode());

                return purchaseDetailEntity1;
            }).collect(Collectors.toList());

            purchaseDetailService.updateBatchById(collect1);
        });
    }

    /**
     * 完成采购需求
     *
     * @param doneVo
     */
    @Override
    public void done(PurchaseDoneVo doneVo) {
        // 1.改变采购项的状态
        boolean flag = true;
        List<PurchaseDetailEntity> updates = new ArrayList<>();
        List<PurchaseItemDoneVo> items = doneVo.getItems();
        for (PurchaseItemDoneVo item : items) {
            PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
            if (item.getStatus() == WareConstant.PurchaseDetailStatus.FAIL.getCode()) {
                flag = false;
                purchaseDetailEntity.setStatus(item.getStatus());
            } else {
                purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatus.FINISHED.getCode());
                // 3.将成功采购入库
                PurchaseDetailEntity entity = purchaseDetailService.getById(item.getItemId());
                wareSkuService.addStock(entity.getSkuId(),entity.getWareId(),entity.getSkuNum());
            }
            purchaseDetailEntity.setId(item.getItemId());

            updates.add(purchaseDetailEntity);
        }
        purchaseDetailService.updateBatchById(updates);
        // 2.修改采购单的状态
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(doneVo.getId());
        purchaseEntity.setStatus(
                flag ? WareConstant.PurchaseStatus.FINISHED.getCode()
                        : WareConstant.PurchaseStatus.HASEXCEPTION.getCode());

        purchaseEntity.setUpdateTime(new Date());

        this.updateById(purchaseEntity);

    }

}