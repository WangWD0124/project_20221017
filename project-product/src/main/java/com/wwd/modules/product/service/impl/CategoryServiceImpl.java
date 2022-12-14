package com.wwd.modules.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wwd.common.service.impl.CrudServiceImpl;
import com.wwd.common.utils.ConvertUtils;
import com.wwd.modules.product.dao.CategoryDao;
import com.wwd.modules.product.dto.CategoryDTO;
import com.wwd.modules.product.entity.CategoryEntity;
import com.wwd.modules.product.service.CategoryService;
import com.wwd.modules.product.vo.Catelog2Vo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 商品三级分类
 *
 * @author wwd 1245436962@qq.com
 * @since 1.0.0 2022-10-14
 */
@Service
public class CategoryServiceImpl extends CrudServiceImpl<CategoryDao, CategoryEntity, CategoryDTO> implements CategoryService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public QueryWrapper<CategoryEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");

        QueryWrapper<CategoryEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);

        return wrapper;
    }

    /**
     * 查询全部三级分类，并以树形结构返回
     */
    @Override
    public List<CategoryDTO> listWithTree() {
        List<CategoryEntity> allCategoryEntities = baseDao.selectList(null);//全部分类
        return allCategoryEntities.stream().filter(categoryEntity ->
                categoryEntity.getCatLevel() == 1 //全部一级分类
        ).map(rootCategoryEntity -> {
            CategoryDTO rootCategoryDTO = ConvertUtils.sourceToTarget(rootCategoryEntity, CategoryDTO.class);
            rootCategoryDTO.setChildren(getChildrens(rootCategoryEntity, allCategoryEntities));//为一级分类添加二级分类
            return rootCategoryDTO;
        }).collect(Collectors.toList());

    }

    private List<CategoryDTO> getChildrens(CategoryEntity root, List<CategoryEntity> all) {

        return all.stream().filter(categoryEntity ->
                categoryEntity.getParentCid().equals(root.getCatId())
        ).map(rootCategoryEntity -> {
            CategoryDTO rootCategoryDTO = ConvertUtils.sourceToTarget(rootCategoryEntity, CategoryDTO.class);
            rootCategoryDTO.setChildren(getChildrens(rootCategoryEntity, all));//为上一级分类添加下一级分类
            return rootCategoryDTO;
        }).collect(Collectors.toList());
    }

    @Override
    public Long[] findCatelogPath(Long catelogId) {

        List<Long> catelogPath = new ArrayList<>();
        catelogPath.add(catelogId);
        findParentCid(catelogPath, catelogId);
        Collections.reverse(catelogPath);
        return catelogPath.toArray(new Long[catelogPath.size()]);
    }

    private void findParentCid(List<Long> catelogPath, Long catelogId) {
        Long parentCid = baseDao.getParentCidBycatId(catelogId);
        if (parentCid != 0){
            catelogPath.add(parentCid);
            findParentCid(catelogPath, parentCid);
        }
    }

    @Override
    public String findCategoryName(Long catelogId) {
        return baseDao.getCategoryNameBycatId(catelogId);
    }

    @Override
    public List<CategoryDTO> getCategoryLevel1() {

        LambdaQueryWrapper<CategoryEntity> wrapper = new LambdaQueryWrapper();
        wrapper.eq(CategoryEntity::getCatLevel, 1);
        List<CategoryEntity> categoryEntities = baseDao.selectList(wrapper);
        return ConvertUtils.sourceToTarget(categoryEntities, CategoryDTO.class);
    }

    @Override
    public Map<String, Object> getCatelogJson() {

        //加载缓存数据
        String catelogJSON = stringRedisTemplate.opsForValue().get("catelogJSON");
        //判断是否为空
        if (StringUtils.isEmpty(catelogJSON)){
            //首次从数据库读取
            Map<String, Object> catelogJsonFromDB = getCatelogJsonFromDB();
            //将数据对象转成JSON字符串数据（序列化）写入缓存
            String json = JSON.toJSONString(catelogJsonFromDB);
            stringRedisTemplate.opsForValue().set("catelogJSON", json);
            return catelogJsonFromDB;
        }
        //将JSON字符串数据转成对象（反序列化）
        Map<String, Object> map = JSON.parseObject(catelogJSON, new TypeReference<Map<String, Object>>() {});
        return map;
    }

    public Map<String, Object> getCatelogJsonFromDB() {

        //减少访问数据库次数，一次性读取
        List<CategoryEntity> categoryEntities = baseDao.selectList(null);

        List<CategoryEntity> categoryLevel1Entities = getCategoryByParentCid(categoryEntities, 0L);

        Map<String, Object> map = categoryLevel1Entities.stream().collect(Collectors.toMap(item -> item.getCatId().toString(), item -> {
            return getCategoryByParentCid(categoryEntities, item.getCatId()).stream().map(categoryDTO2 -> {
                Catelog2Vo catelog2Vo = new Catelog2Vo();
                catelog2Vo.setCatelog1Id(categoryDTO2.getParentCid().toString());
                catelog2Vo.setId(categoryDTO2.getCatId().toString());
                catelog2Vo.setName(categoryDTO2.getName());
                List<Catelog2Vo.Catelog3Vo> catelog3VoList = getCategoryByParentCid(categoryEntities, categoryDTO2.getCatId()).stream().map(categoryDTO3 -> {
                    Catelog2Vo.Catelog3Vo catelog3Vo = new Catelog2Vo.Catelog3Vo();
                    catelog3Vo.setCatelog2Id(categoryDTO3.getParentCid().toString());
                    catelog3Vo.setId(categoryDTO3.getCatId().toString());
                    catelog3Vo.setName(categoryDTO3.getName());
                    return catelog3Vo;
                }).collect(Collectors.toList());
                catelog2Vo.setCatelog3List(catelog3VoList);
                return catelog2Vo;
            }).collect(Collectors.toList());
        }));

        return map;
    }

    private List<CategoryEntity> getCategoryByParentCid(List<CategoryEntity> categoryEntities, long parentCid) {
        return categoryEntities.stream().filter(categoryEntity -> categoryEntity.getParentCid()==parentCid).collect(Collectors.toList());
    }
}