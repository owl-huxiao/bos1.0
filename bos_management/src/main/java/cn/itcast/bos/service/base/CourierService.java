package cn.itcast.bos.service.base;

import cn.itcast.bos.domain.base.Courier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface CourierService {
    //保存快递员
    void save(Courier courier);

    Page<Courier> findPageData(Specification<Courier> specification,Pageable pageable);

    void delBatch(String[] idArray);
    void restoreBatch(String[] idArray);
}
