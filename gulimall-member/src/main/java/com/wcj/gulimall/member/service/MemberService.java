package com.wcj.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wcj.common.utils.PageUtils;
import com.wcj.gulimall.member.entity.MemberEntity;

import java.util.Map;

/**
 * 会员
 *
 * @author wuchangjian
 * @email ${email}
 * @date 2023-03-05 09:55:33
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

