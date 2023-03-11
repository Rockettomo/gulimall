package com.wcj.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wcj.common.utils.PageUtils;
import com.wcj.gulimall.ware.entity.PurchaseEntity;
import com.wcj.gulimall.ware.vo.MergeVo;
import com.wcj.gulimall.ware.vo.PurchaseDoneVo;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author wuchangjian
 * @email ${email}
 * @date 2023-03-05 10:09:13
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageUnreceivePurchase(Map<String, Object> params);

    void mergePurchase(MergeVo mergeVo);

    void recevied(List<Long> ids);

    void done(PurchaseDoneVo doneVo);
}

