package com.wcj.gulimall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wcj.gulimall.product.entity.CategoryEntity;
import com.wcj.gulimall.product.service.CategoryService;
import com.wcj.common.utils.PageUtils;
import com.wcj.common.utils.R;



/**
 * 商品三级分类
 *
 * @author wuchangjian
 * @email ${email}
 * @date 2023-03-05 09:05:58
 */
@RestController
@RequestMapping("product/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;



    /**
     * 批量修改
     */
    @RequestMapping("/update/sort")
    public R updateSort(@RequestBody CategoryEntity[] categories){
        categoryService.updateBatchById(Arrays.asList(categories));

        return R.ok();
    }

    /**
     * 列表
     * 查出所有分类以树形结构组装起来
     */
    @RequestMapping("/list/tree")
    public R list(){

        List<CategoryEntity> entities = categoryService.listWithTree();

        return R.ok().put("data", entities);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{catId}")
    public R info(@PathVariable("catId") Long catId){
		CategoryEntity category = categoryService.getById(catId);

        return R.ok().put("data", category);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody CategoryEntity category){
		categoryService.save(category);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody CategoryEntity category){
		categoryService.updateCascade(category);

        return R.ok();
    }

    /**
     * 删除
     * @RequestBody SpringMVC会自动将请求体中的数据转为对象
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] catIds){

        categoryService.removeMenusByIds(Arrays.asList(catIds));

        return R.ok();
    }

}
