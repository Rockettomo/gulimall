package com.wcj.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wcj.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.wcj.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.wcj.gulimall.product.entity.AttrEntity;
import com.wcj.gulimall.product.service.AttrService;
import com.wcj.gulimall.product.vo.AttrGroupRelationVo;
import com.wcj.gulimall.product.vo.AttrGroupWithAttrsVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wcj.common.utils.PageUtils;
import com.wcj.common.utils.Query;

import com.wcj.gulimall.product.dao.AttrGroupDao;
import com.wcj.gulimall.product.entity.AttrGroupEntity;
import com.wcj.gulimall.product.service.AttrGroupService;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    private AttrAttrgroupRelationDao relationDao;
    @Autowired
    private AttrService attrService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catelogId) {
        String key = (String) params.get("key");
        if (catelogId == 0) {
            LambdaQueryWrapper<AttrGroupEntity> wrapper = new LambdaQueryWrapper<>();
            if (!StringUtils.isEmpty(key)) {
                wrapper.like(AttrGroupEntity::getAttrGroupName, key);
            }
            IPage<AttrGroupEntity> page = this.page(
                    new Query<AttrGroupEntity>().getPage(params),
                    wrapper
            );

            return new PageUtils(page);

        } else {
            // select * from pms_attr_group where catelogId = ? and (attr_group_id = key or attr_group_name like key)
            LambdaQueryWrapper<AttrGroupEntity> wrapper = new LambdaQueryWrapper<AttrGroupEntity>()
                    .eq(AttrGroupEntity::getCatelogId, catelogId);
            if (!StringUtils.isEmpty(key)) {
                wrapper.like(AttrGroupEntity::getAttrGroupName, key);
            }
            IPage<AttrGroupEntity> page = this.page(
                    new Query<AttrGroupEntity>().getPage(params),
                    wrapper
            );
            return new PageUtils(page);//PageUtils应该理解为pageInfo
        }
    }

    @Override
    public void deleteRelation(AttrGroupRelationVo[] vos) {
        // TODO
        // 1.得到需要删除的IDs
        ArrayList<Long> ids = new ArrayList<>();
        for (AttrGroupRelationVo vo : vos) {
            AttrAttrgroupRelationEntity relationEntity =
                    relationDao.selectOne(new LambdaQueryWrapper<AttrAttrgroupRelationEntity>()
                            .eq(AttrAttrgroupRelationEntity::getAttrId, vo.getAttrId())
                            .eq(AttrAttrgroupRelationEntity::getAttrGroupId, vo.getAttrGroupId()));
            if (relationEntity != null) {
                ids.add(relationEntity.getId());
            }
        }
        if (ids.size() != 0) {
            // 2.批量删除
            relationDao.deleteBatchIds(ids);
        }
    }

    /**
     * 根据分类ID查询所有组(所有属性的信息)
     *
     * @param catelogId
     * @return
     */
    @Override
    public List<AttrGroupWithAttrsVo> getAttrGroupWithAttrsByCatelogId(Long catelogId) {
        // 1.查询分组信息
        List<AttrGroupEntity> attrGroupEntities =
                this.list(new LambdaQueryWrapper<AttrGroupEntity>()
                        .eq(AttrGroupEntity::getCatelogId, catelogId));

        // 2.查询所有属性
        List<AttrGroupWithAttrsVo> collect = attrGroupEntities.stream().map((attrGroupEntity -> {
            AttrGroupWithAttrsVo attrGroupWithAttrsVo = new AttrGroupWithAttrsVo();
            BeanUtils.copyProperties(attrGroupEntity,attrGroupWithAttrsVo);
            // 按照分组Id获取到所有属性
            attrGroupWithAttrsVo.setAttrs(attrService.getRelationAttr(attrGroupWithAttrsVo.getAttrGroupId()));

            return attrGroupWithAttrsVo;
        })).collect(Collectors.toList());

        return collect;
    }
}