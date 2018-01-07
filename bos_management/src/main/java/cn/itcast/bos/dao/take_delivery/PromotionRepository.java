package cn.itcast.bos.dao.take_delivery;

import cn.itcast.bos.domain.take_delivery.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;

public interface PromotionRepository extends JpaRepository<Promotion,Integer> {
    /**
     * 修改活动状态
     * @param id
     * @param status
     */
    @Query(value = "update Promotion set status = ?2 where id =?1")
    @Modifying
    public void updateStatus(Integer id, String status);

    /**
     * 设置活动过期
     * @param date
     */
    @Query("update Promotion set status = '2' where endDate<? and status='1'")
    @Modifying
    void updateStatus(Date date);
}
