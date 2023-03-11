package com.wcj.gulimall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.wcj.common.constant.WareConstant;
import com.wcj.gulimall.product.entity.ProductAttrValueEntity;
import com.wcj.gulimall.product.service.ProductAttrValueService;
import com.wcj.gulimall.product.vo.AttrGroupRelationVo;
import com.wcj.gulimall.product.vo.AttrRespVo;
import com.wcj.gulimall.product.vo.AttrVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.wcj.gulimall.product.entity.AttrEntity;
import com.wcj.gulimall.product.service.AttrService;
import com.wcj.common.utils.PageUtils;
import com.wcj.common.utils.R;


/**
 * 商品属性
 *
 * @author wuchangjian
 * @email ${email}
 * @date 2023-03-05 09:05:58
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {
    @Autowired(required = false)
    private AttrService attrService;

    @Autowired
    private ProductAttrValueService productAttrValueService;

    /**
     * 查出spu的规格参数
     *
     * @param spuId
     * @return
     */
    @GetMapping("/base/listforspu/{spuId}")
    public R baseAttrListForSpu(@PathVariable("spuId") Long spuId) {
        List<ProductAttrValueEntity> entityList = productAttrValueService.baseAttrListForSpu(spuId);

        return R.ok().put("data", entityList);
    }

    @GetMapping("/{attrType}/list/{catelogId}")
    public R baseAttrList(@RequestParam Map<String, Object> params,
                          @PathVariable("catelogId") Long catelogId,
                          @PathVariable("attrType") String attrType) {
        PageUtils page = attrService.queryAttrPage(params, catelogId, attrType);

        return R.ok().put("page", page);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("product:attr:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = attrService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrId}")
    public R info(@PathVariable("attrId") Long attrId) {
        AttrRespVo respVo = attrService.getAttrInfo(attrId);

        return R.ok().put("attr", respVo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody AttrVo attr) {
        attrService.saveAttr(attr);


        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody AttrVo attr) {
        attrService.updateAttr(attr);

        return R.ok();
    }

    @RequestMapping("/update/{spuId}")
    public R updateSpuAttr(@PathVariable("spuId") Long spuId,
                           @RequestBody List<ProductAttrValueEntity> entities
    ) {
        productAttrValueService.updateSpuAttr(spuId,entities);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:attr:delete")
    public R delete(@RequestBody Long[] attrIds) {
        attrService.removeByIds(Arrays.asList(attrIds));

        return R.ok();
    }

}
