package cn.itcast.bos.service.base;

import cn.itcast.bos.domain.base.Courier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface CourierService {
    //保存快递员
    void save(Courier courier);
    // 分页查询
    Page<Courier> findPageData(Specification<Courier> specification,Pageable pageable);
    // 批量作废
    void delBatch(String[] idArray);
    void restoreBatch(String[] idArray);
    // 查询未关联定区的快递员
    List<Courier> findNoAssociation();
}
