package cn.itcast.bos.service.base;

import cn.itcast.bos.domain.base.Courier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CourierService {

    void save(Courier courier);

    Page<Courier> findPageData(Pageable pageable);
}
