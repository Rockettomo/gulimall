/**
 * Copyright 2023 json.cn
 */
package com.wcj.gulimall.product.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Auto-generated: 2023-03-09 11:24:46
 *
 * @author json.cn (i@json.cn)
 * @website http://www.json.cn/java2pojo/
 */
@Data
public class SpuSaveVo {

    private String spuName;
    private String spuDescription;
    private Long catalogId;
    private Long brandId;
    private BigDecimal weight;
    private Integer publishStatus;
    private List<String> decript;
    private List<String> images;
    private Bounds bounds;
    private List<BaseAttrs> baseAttrs;
    private List<Skus> skus;


}