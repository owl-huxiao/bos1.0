package cn.itcast.bos.web.action.base;

import cn.itcast.bos.domain.base.FixedArea;
import cn.itcast.bos.service.base.FixedAreaService;
import cn.itcast.bos.web.action.common.BaseAction;
import cn.itcast.crm.domain.Customer;
import com.opensymphony.xwork2.ActionContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;

import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.List;

/**
 * 定区管理的Action
 */
@Controller
@Scope("prototype")
@ParentPackage("json-default")
@Namespace("/")
public class FixedAreaAction extends BaseAction<FixedArea> {
   //注入Service
    @Autowired
    private FixedAreaService fixedAreaService;
    @Action(value = "fixedArea_save",results = {
        @Result(name = "success",type = "redirect",location = "./pages/base/fixed_area.html")})
    public String save(){
        //调用业务层
        fixedAreaService.save(model);
        return SUCCESS;
    }

    /**
     * 分页查询
     * @return
     */
    @Action(value = "fixedArea_pageQuery",results = {@Result(name = "success",type = "json")})
    public String pageQuery(){
        Page<FixedArea> pageData = fixedAreaService.findPageData(model,page,rows);
        //压入栈顶
        pushPageDataToValueStack(pageData);
        return SUCCESS;
    }

    /**
     * 查询所有定区
     * @return
     */
    @Action(value = "fixedArea_findAll",results = {@Result(name = "success",type = "json")})
    public String findAll(){
        List<FixedArea> fixedAreas = fixedAreaService.findAll();
        //压入栈顶
        ActionContext.getContext().getValueStack().push(fixedAreas);
        return SUCCESS;
    }

    /**
     *  查询未关联定区列表
     */
    @Action(value = "fixedArea_findNoAssociationCustomers", results = { @Result(name = "success", type = "json") })
    public String findNoAssociationCustomers(){
        // 使用webClient调用 webService接口
        Collection<? extends Customer> collection = WebClient.
                create("http://localhost:9002/crm_management/services/customerService/noAssociationCustomers")
                .accept(MediaType.APPLICATION_JSON).getCollection(Customer.class);
        ActionContext.getContext().getValueStack().push(collection);
        return SUCCESS;
    }
    @Action(value = "fixedArea_findHasAssociationFixedAreaCustomers",results = {
            @Result(name = "success",type = "json")})
    public String findHasAssociationFixedAreaCustomers(){
        //调用 webService接口
        Collection<? extends Customer> collection = WebClient
                .create("http://localhost:9002/crm_management/services/customerService/associationFixedAreaCustomers/"+ model.getId())
                .accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON).getCollection(Customer.class);
        ActionContext.getContext().getValueStack().push(collection);
        return SUCCESS;
    }

    // 属性驱动
    private String[] customerIds;
    public void setCustomerIds(String[] customerIds) {
        this.customerIds = customerIds;
    }
    /**
     * 将客户关联到定区
     */
    @Action(value = "fixedArea_associationCustomersToFixedArea",results =
        @Result(name = "success",type = "redirect",location = "./pages/base/fixed_area.html"))
    public String associationCustomersToFixedArea(){
        String customerIdStr = StringUtils.join(customerIds,",");
        WebClient.create("http://localhost:9002/crm_management/services/customerService/" +
                "associationCustomerToFixedArea?customerIdStr="+customerIdStr
                +"&fixedAreaId="+model.getId()).put(null);
        System.out.println(customerIdStr);
        return SUCCESS;
    }

    //属性驱动
    private Integer courierId;
    private Integer takeTimeId;

    public void setCourierId(Integer courierId) {
        this.courierId = courierId;
    }

    public void setTakeTimeId(Integer takeTimeId) {
        this.takeTimeId = takeTimeId;
    }
    /**
     * 关联快递员到定区
     */
    @Action(value = "fixedArea_associationCourierToFixedArea",results =
        @Result(name = "success",type = "redirect",location = "./pages/base/fixed_area.html"))
    public String associationCourierToFixedArea(){
        //调用业务层
        fixedAreaService.associationCourierToFixedArea(model,courierId,takeTimeId);
        return SUCCESS;
    }

}
