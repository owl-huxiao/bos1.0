package cn.itcast.bos.service.base;

import cn.itcast.bos.domain.base.Area;
import cn.itcast.bos.domain.base.FixedArea;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 定区管理的业务层接口
 */
public interface FixedAreaService {
    void save(FixedArea model);

    /**
     * 分页查询所有定区
     * @param model
     * @param page
     * @param rows
     * @return
     */
    Page<FixedArea> findPageData(FixedArea model, int page, int rows);

    /**
     * 查询所有定区
     * @return
     */
    List<FixedArea> findAll();

    FixedArea findById(String fixedAreaId);
    /**
     * 关联快递员到定区
     */
    void associationCourierToFixedArea(FixedArea fixedArea, Integer courierId, Integer takeTimeId);
}
