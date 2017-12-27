package cn.itcast.bos.web.action.base;

import cn.itcast.bos.domain.base.Area;
import cn.itcast.bos.service.base.AreaService;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@Scope("prototype")
@ParentPackage("json-default")
@Namespace("/")
public class AreaAction extends ActionSupport implements ModelDriven<Area>{
    // 模型驱动
    private Area area = new Area();
    @Override
    public Area getModel() {
        return area;
    }
    @Autowired
    private AreaService areaService;
    // 接收上传文件
    private File file;
    private String fileFileName;

    public void setFile(File file) {
        this.file = file;
    }

    public void setFileFileName(String fileFileName) {
        this.fileFileName = fileFileName;
    }
    // 批量区域数据导入
    @Action(value = "area_batchImport",results = {
            @Result(name = "success",type = "json")})
    public String batchImport(){
        String msg = "";

        try {
            // 1.创建WorkBook对象
            Workbook workbook = null;
            //2.判断后缀名
            if(fileFileName.endsWith(".xls")){
                workbook = new HSSFWorkbook(new FileInputStream(file));
            }else {
                workbook = new XSSFWorkbook(new FileInputStream(file));
            }
            List<Area> areas = new ArrayList<Area>();
            // 3.获取Sheet页
            Sheet sheet = workbook.getSheetAt(0);
            //获得行:row
            for (int i = 1;i<sheet.getLastRowNum();i++){
                // 获取每一行
                Row row =sheet.getRow(i);
                // 获取每一个单元格 cell 并且获取值
                String id = row.getCell(0).getStringCellValue();
                String province = row.getCell(1).getStringCellValue();
                String city = row.getCell(2).getStringCellValue();
                String district = row.getCell(3).getStringCellValue();
                String postcode = row.getCell(4).getStringCellValue();

                Area area = new Area(id, province, city, district, postcode);
                areas.add(area);
            }
            //保存数据
            areaService.saveBatch(areas);
            //成功！！！
            msg = "文件上传成功！";
        } catch (Exception e) {
            e.printStackTrace();
            msg = "文件上传失败!";
        }
        ActionContext.getContext().getValueStack().push(msg);
        return SUCCESS;
    }
}
