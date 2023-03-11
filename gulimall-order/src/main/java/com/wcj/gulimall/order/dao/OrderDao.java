package com.wcj.gulimall.order.dao;

import com.wcj.gulimall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author wuchangjian
 * @email ${email}
 * @date 2023-03-05 10:04:52
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
