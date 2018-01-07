package cn.itcast.bos.dao.base;

import cn.itcast.bos.domain.base.Courier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

public interface CourierRepository extends JpaRepository<Courier,Integer>,
        JpaSpecificationExecutor<Courier>{
    @Query(value = "update Courier set deltag=?2 where id = ?1")
    @Modifying
    public void updateDeltag(Integer id,Character flag);
}
