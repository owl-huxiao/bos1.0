package cn.itcast.bos.domain.page;

import cn.itcast.bos.domain.take_delivery.Promotion;
import org.apache.struts2.convention.annotation.Result;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import java.util.List;

/**
 * 自定义分页数据封装对象
 * @param <T>
 */
@XmlRootElement(name = "pageBean")
@XmlSeeAlso({Promotion.class})
public class PageBean<T> {
    private long totalCount;//总记录数
    private List<T> pageData;// 当前页数据

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public List<T> getPageData() {
        return pageData;
    }

    public void setPageData(List<T> pageData) {
        this.pageData = pageData;
    }
}
