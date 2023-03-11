package com.wcj.gulimall.member.dao;

import com.wcj.gulimall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author wuchangjian
 * @email ${email}
 * @date 2023-03-05 09:55:33
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
