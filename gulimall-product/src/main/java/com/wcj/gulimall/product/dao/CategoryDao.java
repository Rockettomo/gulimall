package com.wcj.gulimall.product.dao;

import com.wcj.gulimall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author wuchangjian
 * @email wuchangjian@gmail.com
 * @date 2023-03-04 16:59:22
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
