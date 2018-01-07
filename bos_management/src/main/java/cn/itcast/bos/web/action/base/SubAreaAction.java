package cn.itcast.bos.web.action.base;

import cn.itcast.bos.domain.base.Area;
import cn.itcast.bos.domain.base.FixedArea;
import cn.itcast.bos.domain.base.SubArea;
import cn.itcast.bos.service.base.AreaService;
import cn.itcast.bos.service.base.FixedAreaService;
import cn.itcast.bos.service.base.SubAreaService;
import cn.itcast.bos.web.action.common.BaseAction;
import com.opensymphony.xwork2.ActionContext;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;

import javax.servlet.ServletOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@ParentPackage("json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class SubAreaAction extends BaseAction<SubArea> {
    @Autowired
    private SubAreaService subAreaService;
    @Autowired
    private AreaService areaService;
    @Autowired
    private FixedAreaService fixedAreaService;
    @Action(value = "subArea_save", results = {
            @Result(name = "success", type = "redirect", location = "./pages/base/sub_area.html")})
    public String save() {
        subAreaService.save(model);
        return SUCCESS;
    }
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
    @Action(value = "subArea_batchImport", results = {
            @Result(name = "success", type = "json")})
    public String batchImport() {
        String msg = "";
        try {
            // 1.创建WorkBook对象
            Workbook workbook = null;
            //2.判断后缀名
            if (fileFileName.endsWith(".xls")) {
                workbook = new HSSFWorkbook(new FileInputStream(file));
            } else {
                workbook = new XSSFWorkbook(new FileInputStream(file));
            }
            List<SubArea> subAreas = new ArrayList<SubArea>();
            // 3.获取Sheet页
            Sheet sheet = workbook.getSheetAt(0);
            //获得行:row
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                // 获取每一行
                Row row = sheet.getRow(i);
                // 获取每一个单元格 cell 并且获取值
                //获取分区编号
                String id = row.getCell(0).getStringCellValue();
                //获取定区编码
                String fixedAreaId = row.getCell(1).getStringCellValue();
                //FixedArea fixedArea = fixedAreaService.findById(fixedAreaId);
                FixedArea fixedArea = new FixedArea();
                fixedArea.setId(fixedAreaId);
                //获取区域编码
                String areaId = row.getCell(2).getStringCellValue();
                //Area area = areaService.findById(areaId);
                Area area =new Area();
                area.setId(areaId);
                //关键字
                String keyWords = row.getCell(3).getStringCellValue();
                //起始号
                String startNum = row.getCell(4).getStringCellValue();
                //结束号
                String endNum = row.getCell(5).getStringCellValue();
                //单双号
                Character single = row.getCell(6).getStringCellValue().charAt(0);
                //位置信息
                String assistKeyWords = row.getCell(7).getStringCellValue();
                SubArea subArea = new SubArea(id, startNum, endNum, single, keyWords, assistKeyWords, area, fixedArea);

                subAreas.add(subArea);
            }
            //保存数据
            subAreaService.saveBatch(subAreas);
            //成功！！！
            msg = "文件上传成功！";
        } catch (Exception e) {
            e.printStackTrace();
            msg = "文件上传失败!";
        }
        ActionContext.getContext().getValueStack().push(msg);
        return SUCCESS;
    }
    /**
     * 分页查询
     * @return
     */
    @Action(value = "subArea_pageQuery",results = {@Result(name = "success",type = "json")})
    public String pageQuery(){
        Page<SubArea> pageData = subAreaService.findPageData(model,page,rows);
        // 压入值栈
        pushPageDataToValueStack(pageData);
        return SUCCESS;
    }
    /**
     * 文件的导出功能
     * @return
     */
    @Action(value = "subArea_export")
    public String export(){
        //1.查询所有数据
        List<SubArea> subAreaList =subAreaService.findAll();
        //2.将数据写入excel文件
        //2.1创建wookbook对象
        Workbook workbook = new XSSFWorkbook();
        //规定5000条数据一个Sheet
        int size = subAreaList.size();
        int pageSize = 5000;
        //总的sheet数
        int countSheet = (int) Math.ceil((size*1.0/pageSize));
        for (int i = 0; i <= countSheet;i++){
            //2.2创建Sheet
            Sheet sheet = workbook.createSheet("区域数据"+i);
            //2.3创建row 表头行
            Row row = sheet.createRow(0);
            row.createCell(0).setCellValue("分区编号");
            row.createCell(1).setCellValue("定区编码");
            row.createCell(2).setCellValue("区域编码");
            row.createCell(3).setCellValue("关键字");
            row.createCell(4).setCellValue("起始号");
            row.createCell(5).setCellValue("结束号");
            row.createCell(6).setCellValue("单双号");
            row.createCell(7).setCellValue("位置信息");
            //2.4遍历集合，创建数据行
            for(int j = i * pageSize; j < (i+1)*pageSize; j++){
                if(j >= size-1){
                    break;
                }
                Row row1 = sheet.createRow(sheet.getLastRowNum()+1);
                row1.createCell(0).setCellValue(subAreaList.get(j).getId());
                row1.createCell(1).setCellValue(subAreaList.get(j).getFixedArea().getId());
                row1.createCell(2).setCellValue(subAreaList.get(j).getArea().getId());
                row1.createCell(3).setCellValue(subAreaList.get(j).getKeyWords());
                row1.createCell(4).setCellValue(subAreaList.get(j).getStartNum());
                row1.createCell(5).setCellValue(subAreaList.get(j).getEndNum());
                row1.createCell(6).setCellValue(subAreaList.get(j).getSingle().toString());
                row1.createCell(7).setCellValue(subAreaList.get(j).getAssistKeyWords());
            }
        }
        try {
            //3.将excel文件写入客户端,提供文件下载
            String filename = "fqsj.xlsx";
            //设置两个头一个流;
            ServletOutputStream outputStream = ServletActionContext.getResponse().getOutputStream();
            //content-type
            ServletActionContext.getResponse().setContentType(
                    ServletActionContext.getServletContext().getMimeType(filename));
            //content-disposition
            ServletActionContext.getResponse().setHeader(
                    "content-disposition","attachment;filename="+filename);
            workbook.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return NONE;
    }
    //属性驱动,接收删除数据的i的字符串
    public String ids;

    public void setIds(String ids) {
        this.ids = ids;
    }

    /**
     * 批量删除功能
     * @return
     */
    @Action(value = "subArea_delBatch",
            results = @Result(name = "success",type ="redirect",location = "./pages/base/sub_area.html"))
    public String delBetch(){
        // 按,分隔ids
        String[] idArray = ids.split(",");
        // 调用业务层，批量作废
        subAreaService.delBatch(idArray);
        return SUCCESS;
    }

}















