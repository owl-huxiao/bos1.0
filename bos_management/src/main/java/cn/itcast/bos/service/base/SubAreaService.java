package cn.itcast.bos.service.base;

import cn.itcast.bos.domain.base.FixedArea;
import cn.itcast.bos.domain.base.SubArea;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 分区管理的服务层接口
 */
public interface SubAreaService {
    /**
     * 添加分区
     * @param model
     */
    void save(SubArea model);

    Page<SubArea> findPageData(SubArea model, int page, int rows);

    /**
     * 批量保存的方法
     * @param subAreas
     */
   void saveBatch(List<SubArea> subAreas);

    /**
     * 批量查询
     * @return
     */
    List<SubArea> findAll();

    /**
     * 批量删除
     * @param idArray
     */
    void delBatch(String[] idArray);
}
