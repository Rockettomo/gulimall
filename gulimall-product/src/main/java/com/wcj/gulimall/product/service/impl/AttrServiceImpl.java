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
        // 1.保存基本信息
        this.save(attrEntity);
        // 2.保存属性的关联关系(base属性才有分组)
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
        // 判断类型
        LambdaQueryWrapper<AttrEntity> wrapper =
                new LambdaQueryWrapper<AttrEntity>()
                        .eq(AttrEntity::getAttrType, "base".equalsIgnoreCase(attrType)
                                ? ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()
                                : ProductConstant.AttrEnum.ATTRT_TYPE_SALE.getCode());

        // 判断是否点击了左侧三级分类
        if (catelogId != 0) {
            wrapper.eq(AttrEntity::getCatelogId, catelogId);
        }
        // 判断是否输入关键字查询
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
            // 设置分类和分组的名字(销售属性是不存在分组的)
            if ("base".equalsIgnoreCase(attrType)) {
                // 1. 查询属性所在的组,需要查询中间表 pms_attr_group 获取到所在的组的Id
                AttrAttrgroupRelationEntity relationEntity = relationDao.selectOne(
                        new LambdaQueryWrapper<AttrAttrgroupRelationEntity>()
                                .eq(AttrAttrgroupRelationEntity::getAttrId, attrEntity.getAttrId()));

                if (relationEntity != null && relationEntity.getAttrGroupId() != null) {
                    AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(relationEntity.getAttrGroupId());
                    attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
                }
            }
            // 2. 查询所在分类的分类名称,直接使用分类ID进行查询
            CategoryEntity categoryEntity = categoryDao.selectById(attrEntity.getCatelogId());
            if (categoryEntity != null) {
                attrRespVo.setCatelogName(categoryEntity.getName());
            }
            return attrRespVo;
        }).collect(Collectors.toList());

        // 重新设置pageUtils的结果集
        pageUtils.setList(respVoList);

        return pageUtils;
    }

    /**
     * 查询当前属性的全部信息(包括所在分类的完整路径)
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
            // 1.去关联表查询分组信息并设置分组信息
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

        // 2. 设置分类的信息
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
     * 修改属性
     *
     * @param attr
     */
    @Transactional
    @Override
    public void updateAttr(AttrVo attr) {
        // 1.修改基本属性
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr, attrEntity);

        this.updateById(attrEntity);

        if (attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            // 2.修改分组关联
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
                // 修改
                relationDao.update(relationEntity, updateWrapper);
            } else {
                // 新增
                relationDao.insert(relationEntity);
            }
        }
    }

    /**
     * 根据分组Id,找出对应的所有属性
     *
     * @param attrgroupId
     * @return
     */
    @Override
    public List<AttrEntity> getRelationAttr(Long attrgroupId) {
        // 1.在中间表中查到所有的 属性ID
        List<AttrAttrgroupRelationEntity> entities = relationDao.selectList(
                new LambdaQueryWrapper<AttrAttrgroupRelationEntity>()
                        .eq(AttrAttrgroupRelationEntity::getAttrGroupId, attrgroupId));
        if (entities.size() != 0) {
            List<Long> attrIds = entities.stream()
                    .map(AttrAttrgroupRelationEntity::getAttrId)
                    .collect(Collectors.toList());

            // 2.查出所有属性
            return this.listByIds(attrIds);
        } else {
            return new ArrayList<>();
        }

    }

    /**
     * 获取当前分组没有关联的属性
     *
     * @param params
     * @param attrgroupId
     * @return
     */
    @Override
    public PageUtils getNoRelationAttr(Map<String, Object> params, Long attrgroupId) {
        // 1.当前分组只能关联所属分类里面的所有属性
        Long catelogId = attrGroupDao.selectById(attrgroupId).getCatelogId();

        // 2.当前分组只能关联别的分组没有关联的属性
        // 2.1 找到当前分类下的其他分组
        List<AttrGroupEntity> attrGroupEntities = attrGroupDao.selectList(
                new LambdaQueryWrapper<AttrGroupEntity>()
                        .eq(AttrGroupEntity::getCatelogId, catelogId)  // 其他分组
        );
        List<Long> groupIds = attrGroupEntities.stream()
                .map(AttrGroupEntity::getAttrGroupId).collect(Collectors.toList());
        // 2.2 找到这些分组关联的属性(先去关系表中找出属性的ID)

        if (groupIds.size() != 0) {
            List<AttrAttrgroupRelationEntity> relationEntities = relationDao.selectList(
                    new LambdaQueryWrapper<AttrAttrgroupRelationEntity>()
                            .in(AttrAttrgroupRelationEntity::getAttrGroupId, groupIds)
            );
            List<Long> attrIds = relationEntities.stream()
                    .map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toList());
            // 2.3 从属性表中剔除以上的属性

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