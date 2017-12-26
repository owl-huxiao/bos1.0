package cn.itcast.bos.service.base;

import cn.itcast.bos.domain.base.Standard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 收派标准管理
 */
public interface StandardService {

    void save(Standard standard);

    Page<Standard> findPageDate(Pageable pageable);

    List<Standard> findAll();
}
