package cn.itcast.bos.service.take_delivery.impl;

import cn.itcast.bos.dao.take_delivery.PromotionRepository;
import cn.itcast.bos.domain.page.PageBean;
import cn.itcast.bos.domain.take_delivery.Promotion;
import cn.itcast.bos.service.take_delivery.PromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional
public class PromotionServiceImpl implements PromotionService {
    @Autowired
    private PromotionRepository promotionRepository;
    /**
     * 完成活动任务数据保存
     * @param promotion
     */
    @Override
    public void save(Promotion promotion) {
        promotionRepository.save(promotion);
    }
    /**
     * 分页查询
     * @param pageable
     * @return
     */
    @Override
    public Page<Promotion> findPageData(Pageable pageable) {
        return promotionRepository.findAll(pageable);
    }

    /**
     * 宣传的批量作废
     * @param idArray
     */
    @Override
    public void delBatch(String[] idArray) {
       //调用dao层实现update修改操作,将status修改为2
        for(String idStr :idArray){
            Integer id = Integer.parseInt(idStr);
            promotionRepository.updateStatus(id,"2");
        }
    }
    /**
     * 根据page和rows 返回分页数据
     * @param page
     * @param rows
     * @return
     */
    @Override
    public PageBean<Promotion> findPageData(int page, int rows) {
        Pageable pageable = new PageRequest(page-1,rows);
        Page<Promotion> pageData = promotionRepository.findAll(pageable);
        //将数据封装到pageBean
        PageBean<Promotion> pageBean = new PageBean<Promotion>();
        pageBean.setTotalCount(pageData.getTotalElements());
        pageBean.setPageData(pageData.getContent());
        return pageBean;
    }
    /**
     * 根据id查询
     * @param id
     * @return
     */
    @Override
    public Promotion findById(Integer id) {
        return promotionRepository.findOne(id);
    }
    /**
     * 设置活动过期
     * @param date
     */
    @Override
    public void updateStatus(Date date) {
        promotionRepository.updateStatus(date);
    }
}
