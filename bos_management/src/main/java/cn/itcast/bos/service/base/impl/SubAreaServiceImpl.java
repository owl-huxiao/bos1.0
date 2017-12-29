package cn.itcast.bos.service.base.impl;


import cn.itcast.bos.dao.base.SubAreaRepository;
import cn.itcast.bos.domain.base.FixedArea;
import cn.itcast.bos.domain.base.SubArea;
import cn.itcast.bos.service.base.SubAreaService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
/**
 * 分区管理的业务实现
 */
@Service
@Transactional
public class SubAreaServiceImpl implements SubAreaService {
    @Autowired
    private SubAreaRepository subAreaRepository;
    /**
     * 添加分区
     * @param model
     */
    @Override
    public void save(SubArea model) {
        subAreaRepository.save(model);
    }

    @Override
    public void saveBatch(List<SubArea> subAreas) {
        subAreaRepository.save(subAreas);
    }

   @Override
    public Page<SubArea> findPageData(final SubArea model, int page, int rows) {
        //1.根据page和 rows 封装pageable对象
        Pageable pageable = new PageRequest(page-1,rows);
       //构造分页查询对象
        Specification<SubArea> specification = new Specification<SubArea>() {
            @Override
            public Predicate toPredicate(
                    Root<SubArea> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> list = new ArrayList<Predicate>();
                // 构造查询条件
                // 多表查询
                //使用aubArea关联Area
                Join<Object, Object> areaJoin = root.join("area", JoinType.INNER);
                if (model.getArea() != null && StringUtils.isNotBlank(model.getArea().getProvince())) {
                    Predicate p2 = cb.like(areaJoin.get("province").as(String.class),
                            "%" + model.getArea().getProvince() + "%");
                    list.add(p2);
                }
                if (model.getArea() != null && StringUtils.isNotBlank(model.getArea().getCity())) {
                    Predicate p3 = cb.like(areaJoin.get("city").as(String.class),
                            "%" + model.getArea().getCity() + "%");
                    list.add(p3);
                }
                if (model.getArea() != null && StringUtils.isNotBlank(model.getArea().getDistrict())) {
                    Predicate p4 = cb.like(areaJoin.get("district").as(String.class),
                            "%"+model.getArea().getDistrict()+"%");
                    list.add(p4);
                }
               Join<Object, Object> fixedAreaJoin = root.join("fixedArea", JoinType.INNER);
                if (model.getFixedArea()!=null && StringUtils.isNotBlank(model.getFixedArea().getId())){
                    Predicate p5 = cb.equal(fixedAreaJoin.get("id").as(String.class),
                            model.getFixedArea().getId());
                    list.add(p5);

                }
                if (StringUtils.isNotBlank(model.getKeyWords())) {
                    // 根据分区关键字
                    Predicate p6 = cb.like(root.get("keyWords").as(String.class),
                            "%"+model.getKeyWords()+"%");
                    list.add(p6);
                }
                return cb.and(list.toArray(new Predicate[0]));
            }
        };
        Page<SubArea> pageData = subAreaRepository.findAll(specification,pageable);
        return pageData;
    }

    @Override
    public List<SubArea> findAll() {
        return subAreaRepository.findAll();
    }

    /**
     * 批量删除
     * @param idArray
     */
    @Override
    public void delBatch(String[] idArray) {
        // 调用DAO实现 delete删除操作
        for (String id:idArray){
            subAreaRepository.delete(id);
        }
    }
}
