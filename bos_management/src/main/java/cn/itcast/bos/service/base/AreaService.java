package cn.itcast.bos.service.base;

import cn.itcast.bos.domain.base.Area;
import org.springframework.data.domain.Page;

import java.util.List;

public interface AreaService {
    /**
     * 保存区域的集合
     * @param areas
     */
    void saveBatch(List<Area> areas);

    /**
     * 分页查询所有区域
     * @param area
     * @param page
     * @param rows
     * @return
     */
    Page<Area> findPageData(Area area, int page, int rows);

    /**
     * 查询所有的区域
     * @return
     */
    List<Area> findAll();

    Area findById(String areaId);
}
