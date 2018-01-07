package cn.itcast.bos.dao.base;

import cn.itcast.bos.domain.base.TakeTime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TakeTimeRepository extends JpaRepository<TakeTime,Integer>{
}
