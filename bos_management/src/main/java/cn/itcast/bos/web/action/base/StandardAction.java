package cn.itcast.bos.web.action.base;

import cn.itcast.bos.domain.base.Standard;
import cn.itcast.bos.service.base.StandardService;
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
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ParentPackage("json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class StandardAction extends ActionSupport implements
		ModelDriven<Standard> {

	// 模型驱动
	private Standard standard = new Standard();
	@Override
	public Standard getModel() {
		return standard;
	}
	// 注入Service对象
	@Autowired
	private StandardService standardService;
	//属性驱动
	private int page;
	private int rows;

	public void setPage(int page) {
		this.page = page;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	// 添加操作
	@Action(value = "standard_save", results = {
			@Result(name = "success", type = "redirect", location = "./pages/base/standard.html") })
	public String save() {
		System.out.println("添加收派标准....");
		standardService.save(standard);
		return SUCCESS;
	}
	//分页查询
	@Action(value = "standard_pageQuery" ,results = {@Result(name = "success",type = 	"json")})
	public String pageQuery(){
		//调用业务层
		Pageable pageable = new PageRequest(page-1,rows);
		Page<Standard> pageData = standardService.findPageDate(pageable);
		//返回页面数据需要tatal rows
		Map<String,Object> result = new HashMap<String, Object>();
		result.put("total",pageData.getTotalElements());
		result.put("rows",pageData.getContent());

		// 将map转换为json数据返回 ，使用struts2-json-plugin 插件
		ActionContext.getContext().getValueStack().push(result);
		return SUCCESS;
	}
	@Action(value = "standard_findAll",results = {@Result(name = "success",type = "json")})
	public String findAll(){
		List<Standard> standards = standardService.findAll();
		ActionContext.getContext().getValueStack().push(standards);
		return SUCCESS;
	}

}
