package com.wcj.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.wcj.common.constant.ProductConstant;
import com.wcj.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.wcj.gulimall.product.dao.AttrGroupDao;
import com.wcj.gulimall.product.dao.CategoryDao;
import com.wcj.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.wcj.gulimall.product.entity.AttrGroupEntity;
import com.wcj.gulimall.product.entity.CategoryEntity;
import com.wcj.gulimall.product.service.CategoryService;
import com.wcj.gulimall.product.vo.AttrGroupRelationVo;
import com.wcj.gulimall.product.vo.AttrRespVo;
import com.wcj.gulimall.product.vo.AttrVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wcj.common.utils.PageUtils;
import com.wcj.common.utils.Query;

import com.wcj.gulimall.product.dao.AttrDao;
import com.wcj.gulimall.product.entity.AttrEntity;
import com.wcj.gulimall.product.service.AttrService;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Attr;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Autowired
    private AttrAttrgroupRelationDao relationDao;
    @Autowired
    private AttrGroupDao attrGroupDao;
    @Autowired
    private CategoryDao categoryDao;
    @Autowired
    private CategoryService categoryService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveAttr(AttrVo attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr, attrEntity);
        // 1.??????????????????
        this.save(attrEntity);
        // 2.???????????????????????????(base??????????????????)
        if (attr.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()
                && attr.getAttrGroupId() != null) {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            relationEntity.setAttrGroupId(attr.getAttrGroupId());
            relationEntity.setAttrId(attrEntity.getAttrId());

            relationDao.insert(relationEntity);
        }

    }

    /**
     * @param params
     * @param catelogId
     * @return
     */
    @Override
    public PageUtils queryAttrPage(Map<String, Object> params, Long catelogId, String attrType) {
        // ????????????
        LambdaQueryWrapper<AttrEntity> wrapper =
                new LambdaQueryWrapper<AttrEntity>()
                        .eq(AttrEntity::getAttrType, "base".equalsIgnoreCase(attrType)
                                ? ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()
                                : ProductConstant.AttrEnum.ATTRT_TYPE_SALE.getCode());

        // ???????????????????????????????????????
        if (catelogId != 0) {
            wrapper.eq(AttrEntity::getCatelogId, catelogId);
        }
        // ?????????????????????????????????
        String key = (String) params.get("key");
        if (StringUtils.isNotEmpty(key)) {
            wrapper.and(obj -> {
                obj.eq(AttrEntity::getAttrId, key).or().like(AttrEntity::getAttrName, key);
            });
        }
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                wrapper
        );

        PageUtils pageUtils = new PageUtils(page);
        List<AttrEntity> records = page.getRecords();
        List<AttrRespVo> respVoList = records.stream().map((attrEntity) -> {
            AttrRespVo attrRespVo = new AttrRespVo();
            BeanUtils.copyProperties(attrEntity, attrRespVo);
            // ??????????????????????????????(?????????????????????????????????)
            if ("base".equalsIgnoreCase(attrType)) {
                // 1. ????????????????????????,????????????????????? pms_attr_group ????????????????????????Id
                AttrAttrgroupRelationEntity relationEntity = relationDao.selectOne(
                        new LambdaQueryWrapper<AttrAttrgroupRelationEntity>()
                                .eq(AttrAttrgroupRelationEntity::getAttrId, attrEntity.getAttrId()));

                if (relationEntity != null && relationEntity.getAttrGroupId() != null) {
                    AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(relationEntity.getAttrGroupId());
                    attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
                }
            }
            // 2. ?????????????????????????????????,??????????????????ID????????????
            CategoryEntity categoryEntity = categoryDao.selectById(attrEntity.getCatelogId());
            if (categoryEntity != null) {
                attrRespVo.setCatelogName(categoryEntity.getName());
            }
            return attrRespVo;
        }).collect(Collectors.toList());

        // ????????????pageUtils????????????
        pageUtils.setList(respVoList);

        return pageUtils;
    }

    /**
     * ?????????????????????????????????(?????????????????????????????????)
     *
     * @param attrId
     * @return
     */
    @Override
    public AttrRespVo getAttrInfo(Long attrId) {
        AttrRespVo respVo = new AttrRespVo();
        AttrEntity attrEntity = this.getById(attrId);
        BeanUtils.copyProperties(attrEntity, respVo);

        if (attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            // 1.???????????????????????????????????????????????????
            AttrAttrgroupRelationEntity relationEntity = relationDao.selectOne(
                    new LambdaQueryWrapper<AttrAttrgroupRelationEntity>()
                            .eq(AttrAttrgroupRelationEntity::getAttrId, attrId)
            );
            if (relationEntity != null) {
                respVo.setAttrGroupId(relationEntity.getAttrGroupId());
                AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(relationEntity.getAttrGroupId());
                if (attrGroupEntity != null) {
                    respVo.setGroupName(attrGroupEntity.getAttrGroupName());
                }
            }
        }

        // 2. ?????????????????????
        Long catelogId = attrEntity.getCatelogId();
        Long[] catelogPath = categoryService.findCatelogPath(catelogId);
        respVo.setCatelogPath(catelogPath);
        CategoryEntity categoryEntity = categoryDao.selectById(catelogId);
        if (categoryEntity != null) {
            respVo.setCatelogName(categoryEntity.getName());
        }

        return respVo;
    }

    /**
     * ????????????
     *
     * @param attr
     */
    @Transactional
    @Override
    public void updateAttr(AttrVo attr) {
        // 1.??????????????????
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr, attrEntity);

        this.updateById(attrEntity);

        if (attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            // 2.??????????????????
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            relationEntity.setAttrGroupId(attr.getAttrGroupId());
            relationEntity.setAttrId(attr.getAttrId());

            LambdaUpdateWrapper<AttrAttrgroupRelationEntity> updateWrapper =
                    new LambdaUpdateWrapper<AttrAttrgroupRelationEntity>()
                            .eq(AttrAttrgroupRelationEntity::getAttrId, attr.getAttrId());

            Long count = relationDao.selectCount(
                    new LambdaQueryWrapper<AttrAttrgroupRelationEntity>()
                            .eq(AttrAttrgroupRelationEntity::getAttrId, attr.getAttrId()));
            if (count > 0) {
                // ??????
                relationDao.update(relationEntity, updateWrapper);
            } else {
                // ??????
                relationDao.insert(relationEntity);
            }
        }
    }

    /**
     * ????????????Id,???????????????????????????
     *
     * @param attrgroupId
     * @return
     */
    @Override
    public List<AttrEntity> getRelationAttr(Long attrgroupId) {
        // 1.?????????????????????????????? ??????ID
        List<AttrAttrgroupRelationEntity> entities = relationDao.selectList(
                new LambdaQueryWrapper<AttrAttrgroupRelationEntity>()
                        .eq(AttrAttrgroupRelationEntity::getAttrGroupId, attrgroupId));
        if (entities.size() != 0) {
            List<Long> attrIds = entities.stream()
                    .map(AttrAttrgroupRelationEntity::getAttrId)
                    .collect(Collectors.toList());

            // 2.??????????????????
            return this.listByIds(attrIds);
        } else {
            return new ArrayList<>();
        }

    }

    /**
     * ???????????????????????????????????????
     *
     * @param params
     * @param attrgroupId
     * @return
     */
    @Override
    public PageUtils getNoRelationAttr(Map<String, Object> params, Long attrgroupId) {
        // 1.?????????????????????????????????????????????????????????
        Long catelogId = attrGroupDao.selectById(attrgroupId).getCatelogId();

        // 2.?????????????????????????????????????????????????????????
        // 2.1 ????????????????????????????????????
        List<AttrGroupEntity> attrGroupEntities = attrGroupDao.selectList(
                new LambdaQueryWrapper<AttrGroupEntity>()
                        .eq(AttrGroupEntity::getCatelogId, catelogId)  // ????????????
        );
        List<Long> groupIds = attrGroupEntities.stream()
                .map(AttrGroupEntity::getAttrGroupId).collect(Collectors.toList());
        // 2.2 ?????????????????????????????????(?????????????????????????????????ID)

        if (groupIds.size() != 0) {
            List<AttrAttrgroupRelationEntity> relationEntities = relationDao.selectList(
                    new LambdaQueryWrapper<AttrAttrgroupRelationEntity>()
                            .in(AttrAttrgroupRelationEntity::getAttrGroupId, groupIds)
            );
            List<Long> attrIds = relationEntities.stream()
                    .map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toList());
            // 2.3 ????????????????????????????????????

            LambdaQueryWrapper<AttrEntity> wrapper = new LambdaQueryWrapper<AttrEntity>()
                    .eq(AttrEntity::getCatelogId, catelogId)
                    .eq(AttrEntity::getAttrType, ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode());

            if (attrIds.size() != 0) {
                wrapper.notIn(AttrEntity::getAttrId, attrIds);
            }
            String key = (String) params.get("key");
            if (StringUtils.isNotEmpty(key)) {
                wrapper.and((w) -> {
                    w.eq(AttrEntity::getAttrId, key).or().like(AttrEntity::getAttrName, key);
                });
            }

            IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), wrapper);
            return new PageUtils(page);
        } else {
            return new PageUtils(this.page(new Query<AttrEntity>().getPage(params)));
        }
    }


}