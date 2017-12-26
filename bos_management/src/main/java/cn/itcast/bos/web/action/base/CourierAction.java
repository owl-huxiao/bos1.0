package cn.itcast.bos.web.action.base;

import cn.itcast.bos.domain.base.Courier;
import cn.itcast.bos.domain.base.Standard;
import cn.itcast.bos.service.base.CourierService;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.HashMap;
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
        Pageable pageable = new PageRequest(page-1,rows);
        Page<Courier> pageData = courierService.findPageData(pageable);
        //返回页面数据需要tatal rows
        Map<String,Object> result = new HashMap<String, Object>();
        result.put("total",pageData.getTotalElements());
        result.put("rows",pageData.getContent());
        ActionContext.getContext().getValueStack().push(result);
        System.out.println(result);
        return SUCCESS;
    }
}
