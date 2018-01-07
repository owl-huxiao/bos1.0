package cn.itcast.bos.service.base;

import cn.itcast.bos.domain.base.TakeTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 收派时间 接口
 */
public interface TakeTimeService {
    // 查询所有收派时间
    public List<TakeTime> findAll();
}
