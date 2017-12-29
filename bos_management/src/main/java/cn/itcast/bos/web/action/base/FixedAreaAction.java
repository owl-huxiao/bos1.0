package cn.itcast.bos.web.action.base;

import cn.itcast.bos.domain.base.FixedArea;
import cn.itcast.bos.service.base.FixedAreaService;
import cn.itcast.bos.web.action.common.BaseAction;
import com.opensymphony.xwork2.ActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;

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

}
