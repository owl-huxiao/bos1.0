package cn.itcast.bos.service.base.impl;

import cn.itcast.bos.dao.base.AreaRepository;
import cn.itcast.bos.domain.base.Area;
import cn.itcast.bos.service.base.AreaService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import javax.persistence.Id;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
@Service
@Transactional
public class AreaServiceImpl implements AreaService {
    // 注入Dao
    @Autowired
    private AreaRepository areaRepository;
    @Override
    public void saveBatch(List<Area> areas) {
        areaRepository.save(areas);
    }

    /**
     * 分页查询
     * @param area
     * @param page
     * @param rows
     * @return
     */
    @Override
    public Page<Area> findPageData(final Area area, int page, int rows) {
        //1.根据page和 rows 封装pageable对象
        Pageable pageable = new PageRequest(page-1,rows);
        //根据Area对象封装查询条件查询
        Specification<Area> specification = new Specification<Area>() {
            @Override
            public Predicate toPredicate(Root<Area> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> list = new ArrayList<Predicate>();
                if (StringUtils.isNotBlank(area.getProvince())){
                    Predicate p1 = cb.like(root.get("province").as(String.class),
                            "%"+area.getProvince()+"%");
                    list.add(p1);
                }
                if (StringUtils.isNotBlank(area.getCity())) {
                    Predicate p2 = cb.like(root.get("city").as(String.class),
                            "%" + area.getCity() + "%");
                    list.add(p2);
                }
                if (StringUtils.isNotBlank(area.getDistrict())) {
                    Predicate p3 = cb.like(root.get("district")
                            .as(String.class), "%" + area.getDistrict() + "%");
                    list.add(p3);
                }
                return cb.and(list.toArray(new Predicate[0]));
            }
        };
        Page<Area> pageData = areaRepository.findAll(specification,pageable);
        return pageData;
    }

    /**
     * 查询所有的区域
     * @return
     */
    @Override
    public List<Area> findAll() {
        return areaRepository.findAll();
    }

    @Override
    public Area findById(String areaId) {
        return areaRepository.findOne(areaId);
    }
}
