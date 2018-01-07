package cn.itcast.bos.service.take_delivery;

import cn.itcast.bos.domain.page.PageBean;
import cn.itcast.bos.domain.take_delivery.Promotion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.ws.rs.*;
import java.util.Date;

/**
 * 宣传任务的业务层接口
 */
public interface PromotionService {
    /**
     * 完成活动任务数据保存
     * @param model
     */
    void save(Promotion model);

    /**
     * 分页查询
     * @param pageable
     * @return
     */
    Page<Promotion> findPageData(Pageable pageable);

    /**
     * 宣传的批量作废
     * @param idArray
     */
    void delBatch(String[] idArray);

    /**
     * 根据page和rows 返回分页数据
     * @param page
     * @param rows
     * @return
     */
    @Path("/pageQuery")
    @GET
    @Produces({"application/xml","application/json"})
    PageBean<Promotion> findPageData(@QueryParam("page") int page
            ,@QueryParam("rows") int rows);

    /**
     * 根据id查询
     * @param id
     * @return
     */
    @Path("promotion/{id}")
    @GET
    @Produces({"application/xml","application/json"})
    Promotion findById(@PathParam("id") Integer id);

    /**
     * 设置活动过期
     * @param date
     */
    void updateStatus(Date date);
}
