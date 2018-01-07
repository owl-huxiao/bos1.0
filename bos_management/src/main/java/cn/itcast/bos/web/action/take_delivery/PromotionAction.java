package cn.itcast.bos.web.action.take_delivery;

import cn.itcast.bos.domain.take_delivery.Promotion;
import cn.itcast.bos.service.take_delivery.PromotionService;
import cn.itcast.bos.web.action.common.BaseAction;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.aspectj.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * 宣传活动管理
 */
@Controller
@Scope("prototype")
@ParentPackage("json-default")
@Namespace("/")
public class PromotionAction extends BaseAction<Promotion>{
    private File titleImgFile;
    private String titleImgFileFileName;
    private String titleImgFileContentType;

    public void setTitleImgFile(File titleImgFile) {
        this.titleImgFile = titleImgFile;
    }

    public void setTitleImgFileFileName(String titleImgFileFileName) {
        this.titleImgFileFileName = titleImgFileFileName;
    }

    public void setTitleImgFileContentType(String titleImgFileContentType) {
        this.titleImgFileContentType = titleImgFileContentType;
    }

    @Autowired
    private PromotionService promotionService;

    @Action(value = "promotion_save",results = {
            @Result(name = "success",type = "redirect",location = "./pages/take_delivery/promotion.html")})
    public String save() throws IOException {
        // 宣传图上传、在数据表保存宣传图路径
        //文件保存目录路径
        String savePath = ServletActionContext.getServletContext().getRealPath("/upload/");
        //文件保存目录URL
        String saveUrl = ServletActionContext.getRequest().getContextPath()+"/upload/";
        // 生成随机图片名
        UUID uuid = UUID.randomUUID();
        String ext = titleImgFileFileName.substring(titleImgFileFileName.lastIndexOf("."));
        String randomFileName = uuid+ext;
        // 保存图片 (绝对路径)
        File destFile = new File(savePath,randomFileName);
        System.out.println(destFile.getAbsoluteFile());
        FileUtil.copyFile(titleImgFile,destFile);

        // 将相对工程web的访问路径，保存model中
        model.setTitleImg(ServletActionContext.getRequest().getContextPath()
                +"/upload/"+randomFileName);
        //调用业务层,保存活动任务数据的保存
        promotionService.save(model);
        return SUCCESS;
    }

    /**
     * 分页查询的方法
     * @return
     */
    @Action(value = "promotion_pageQuery",results = {
            @Result(name = "success",type = "json")})
    public String pageQuery(){
        //构造分页查询参数
        Pageable pageable = new PageRequest(page-1,rows);
        //调用业务层完成分页查询
        Page<Promotion> pageData = promotionService.findPageData(pageable);
        //压入值栈
        pushPageDataToValueStack(pageData);
        return SUCCESS;
    }
    //属性驱动
    private String ids;

    public void setIds(String ids) {
        this.ids = ids;
    }

    /**
     * 作废宣传项目
     * @return
     */
    @Action(value = "promotion_delBatch",results = {
            @Result(name = "success",type = "redirect",location = "./pages/take_delivery/promotion.html")})
    public String delBatch(){
        //以,号分割ids
        String[] idArray = ids.split(",");
        //调用业务层,批量作废
        promotionService.delBatch(idArray);
        return SUCCESS;
    }
}
