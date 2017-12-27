package cn.itcast.bos.web.action.base;

import cn.itcast.bos.domain.base.Courier;
import cn.itcast.bos.service.base.CourierService;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Action
@Scope("prototype")
@ParentPackage("json-default")
@Namespace("/")
public class CourierAction extends ActionSupport implements ModelDriven<Courier>{
    // 模型驱动
    private Courier courier = new Courier();
    @Override
    public Courier getModel() {
        return courier;
    }
    @Autowired
    private CourierService courierService;
    @Action(value = "courier_save" ,results = {
            @Result(name = "success",type = "redirect",location = "./pages/base/courier.html")})
    public String save(){
        System.out.println("添加快递员....");
        courierService.save(courier);
        return SUCCESS;
    }
    //属性驱动
    private int page;
    private int rows;

    public void setPage(int page) {
        this.page = page;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }
    @Action(value = "courier_pageQuery",results = {@Result(name = "success",type = "json")})
    public String pageQuery(){
        // 封装Pageable对象
        final Pageable pageable = new PageRequest(page-1,rows);
        //封装条件查询对象Specification (类似Hibernate的QBC查询)
        Specification<Courier> specification = new Specification<Courier>() {
            @Override
            //构造条件查询方法: 如果方法返回null,代表无条件查询
             // Root 用于获取属性字段，CriteriaQuery可以用于简单条件查询，CriteriaBuilder 用于构造复杂条件查询
            public Predicate toPredicate(Root<Courier> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> list = new ArrayList<Predicate>();
                //简单表查询
                if(StringUtils.isNotBlank(courier.getCourierNum())){
                    //进行快递员工号查询
                    //CourierNum=?
                    Predicate p1 = cb.equal(root.get("courierNum").as(String.class),
                            courier.getCourierNum());
                    list.add(p1);
                }
                if (StringUtils.isNotBlank(courier.getCompany())){
                    //进行公司查询,模糊查询
                    //company like %?%
                    Predicate p2 = cb.like(root.get("company").as(String.class),
                            "%"+courier.getCompany()+"%");
                    list.add(p2);
                }
                if (StringUtils.isNotBlank(courier.getType())){
                    //进行快递员类型查询,等值查询
                    //type= ?
                    Predicate p3 = cb.equal(root.get("type").as(String.class),
                            courier.getType());
                    list.add(p3);
                }
                // 多表查询
                //使用Courier关联Standard
                Join<Object, Object> standardJoin = root.join("standard", JoinType.INNER);
                if (courier.getStandard()!=null && StringUtils.isNotBlank(courier.getStandard().getName())){
                    Predicate p4 = cb.like(standardJoin.get("name").as(String.class),
                            "%"+courier.getStandard().getName()+"%");
                    list.add(p4);
                }
                return cb.and(list.toArray(new Predicate[0]));
            }
        };
        // 调用业务层 ，返回 Page
        Page<Courier> pageData = courierService.findPageData(specification,pageable);
        //返回页面数据需要tatal rows
        Map<String,Object> result = new HashMap<String, Object>();
        result.put("total",pageData.getTotalElements());
        result.put("rows",pageData.getContent());
        ActionContext.getContext().getValueStack().push(result);
        System.out.println(result);
        return SUCCESS;
    }
    //属性驱动
    private String ids;

    public void setIds(String ids) {
        this.ids = ids;
    }

    @Action(value = "courier_delBatch",results = {@Result(name = "success",type = "redirect",
            location = "./pages/base/courier.html")})
    public String delBatch(){
        //按,分割ids
        String[] idArray = ids.split(",");
        //调用业务层,批量作废
        courierService.delBatch(idArray);
        return SUCCESS;
    }
    @Action(value = "courier_restoreBatch",results = {@Result(name = "success",type = "redirect",
            location = "./pages/base/courier.html")})
    public String restoreBatch(){
        //按,分割ids
        String[] idArray = ids.split(",");
        //调用业务层,批量作废
        courierService.restoreBatch(idArray);
        return SUCCESS;
    }
}
