package cn.itcast.bos.web.action.base;

import cn.itcast.bos.domain.base.TakeTime;
import cn.itcast.bos.service.base.TakeTimeService;
import cn.itcast.bos.web.action.common.BaseAction;
import com.opensymphony.xwork2.ActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.List;

@ParentPackage("json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class TakeTimeAction extends BaseAction<TakeTime>{
    @Autowired
    private TakeTimeService takeTimeService;
    @Action(value = "takeTime_findAll",results = {
            @Result(name = "success", type = "json")})
    public String findAll(){
        // 调用业务层，查询所有收派时间
        List<TakeTime> takeTimes = takeTimeService.findAll();
        // 压入值栈返回
        ActionContext.getContext().getValueStack().push(takeTimes);
        return SUCCESS;
    }

}
