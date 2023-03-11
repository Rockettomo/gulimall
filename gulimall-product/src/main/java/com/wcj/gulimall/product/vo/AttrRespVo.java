package com.wcj.gulimall.product.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
public class AttrRespVo extends AttrVo {
    /**
     * 分类名称
     */
    private String catelogName;

    /**
     * 分组名称
     */
    private String groupName;

    /**
     * 分类的路径
     */
    private Long[] catelogPath;
}
