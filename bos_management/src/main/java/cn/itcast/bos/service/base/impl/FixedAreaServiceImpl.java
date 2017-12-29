package cn.itcast.bos.service.base.impl;

import cn.itcast.bos.dao.base.FixedAreaRepository;
import cn.itcast.bos.domain.base.FixedArea;
import cn.itcast.bos.service.base.FixedAreaService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

/**
 * 定区管理的业务层实现类
 */
@Service
@Transactional
public class FixedAreaServiceImpl implements FixedAreaService {
    @Autowired
    private FixedAreaRepository fixedAreaRepository;

    @Override
    public void save(FixedArea model) {
        fixedAreaRepository.save(model);
    }

    /**
     * 分页查询所有定区
     * @param model
     * @param page
     * @param rows
     * @return
     */
    @Override
    public Page<FixedArea> findPageData(final FixedArea model, int page, int rows) {
        //1.根据page和 rows 封装pageable对象
        Pageable pageable = new PageRequest(page-1,rows);
        //构造分页查询对象
        Specification<FixedArea> specification = new Specification<FixedArea>() {
            @Override
            public Predicate toPredicate(
                    Root<FixedArea> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> list = new ArrayList<Predicate>();
                // 构造查询条件
                if (StringUtils.isNotBlank(model.getId())) {
                    // 根据 定区编号查询 等值
                    Predicate p1 = cb.equal(root.get("id").as(String.class),
                            model.getId());
                    list.add(p1);
                }
                if (StringUtils.isNotBlank(model.getCompany())) {
                    // 根据公司查询 模糊
                    Predicate p2 = cb.like(
                            root.get("company").as(String.class),
                            "%" + model.getCompany() + "%");
                    list.add(p2);
                }
                return cb.and(list.toArray(new Predicate[0]));
            }
        };
        Page<FixedArea> pageData = fixedAreaRepository.findAll(specification,pageable);
        return pageData;
    }

    /**
     * 查询所有定区
     * @return
     */
    @Override
    public List<FixedArea> findAll() {
        return fixedAreaRepository.findAll();
    }

    @Override
    public FixedArea findById(String fixedAreaId) {
        return fixedAreaRepository.findOne(fixedAreaId);
    }
}
