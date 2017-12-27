package cn.itcast.bos.service.base;

import cn.itcast.bos.domain.base.Area;

import java.util.List;

public interface AreaService {
    void saveBatch(List<Area> areas);
}
