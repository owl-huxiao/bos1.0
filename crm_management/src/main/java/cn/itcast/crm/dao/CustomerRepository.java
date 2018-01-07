package cn.itcast.crm.dao;

import cn.itcast.crm.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer,Integer>{
    //查找定区属性为空的值
    List<Customer> findByFixedAreaIdIsNull();
    //根据fixedAreaId查找已关联的客户
    List<Customer> findByFixedAreaId(String fixedAreaId);

    //为客户设置定区
    @Query("update Customer set fixedAreaId = ? where id = ?")
    @Modifying
    public void updateFixedAreaId(String fixedAreaId, Integer id);
    //取消客户的定区
    @Query("update Customer set fixedAreaId = null where fixedAreaId = ?")
    @Modifying
    void clearFixedAreaId(String fixedAreaId);

    Customer findByTelephone(String telephone);

    @Query("update Customer set type = 1 where telephone = ?")
    @Modifying
    void updateupdateType(String telephone);
}
