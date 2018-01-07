package cn.itcast.bos.web.action.base;

import cn.itcast.bos.domain.base.Area;
import cn.itcast.bos.service.base.AreaService;
import cn.itcast.bos.utils.PinYin4jUtils;
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

/**
 * 地区区管理的Action
 */
@Controller
@Scope("prototype")
@ParentPackage("json-default")
@Namespace("/")
public class AreaAction extends BaseAction<Area> {
    @Autowired
    private AreaService areaService;
    @Action(value = "area_save",results = {
            @Result(name = "success",type = "redirect",location = "./pages/base/area.html")})
    public String save(){
        areaService.save(model);
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

                // 基于pinyin4j生成城市编码和简码
                province = province.substring(0, province.length() - 1);
                city = city.substring(0,city.length()-1);
                district = district.substring(0,district.length()-1);
                // 简码
                String[] headArray = PinYin4jUtils.getHeadByString(province + city + district);
                StringBuffer buffer  = new StringBuffer();
                for (String headStr :headArray){
                    buffer.append(headStr);
                }

                String shortcode = buffer.toString();
                area.setShortcode(shortcode);
                // 城市编码
                String citycode = PinYin4jUtils.hanziToPinyin(city,"");
                area.setCitycode(citycode);
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

    /**
     * 文件的导出功能
     * @return
     */
    @Action(value = "area_export")
    public String export(){
        //1.查询所有数据
        List<Area> areaList = areaService.findAll();
        //List<Area> areaList = new ArrayList<Area>();
        //for (int i = 0; i < 100020; i++) {
            //areaList.add(new Area("qy"+i, "甘肃"+i, "庆阳"+i, "环县"+i, "745717"));
        //}
        //2.将数据写入excel文件
        //2.1创建wookbook对象
        Workbook workbook = new XSSFWorkbook();
        //规定5000条数据一个Sheet
        int size = areaList.size();
        int pageSize = 5000;
        //总的sheet数
        int countSheet = (int) Math.ceil((size*1.0/pageSize));
        for (int i = 0; i <= countSheet;i++){
            //2.2创建Sheet
            Sheet sheet = workbook.createSheet("区域数据"+i);
            //2.3创建row 表头行
            Row row = sheet.createRow(0);
            row.createCell(0).setCellValue("区域编号");
            row.createCell(1).setCellValue("省份");
            row.createCell(2).setCellValue("城市");
            row.createCell(3).setCellValue("区域");
            row.createCell(4).setCellValue("邮编");
            //2.4遍历集合，创建数据行
            for(int j = i * pageSize; j < (i+1)*pageSize; j++){
                if(j >= size-1){
                    break;
                }
                Row row1 = sheet.createRow(sheet.getLastRowNum()+1);
                row1.createCell(0).setCellValue(areaList.get(j).getId());
                row1.createCell(1).setCellValue(areaList.get(j).getProvince());
                row1.createCell(2).setCellValue(areaList.get(j).getCity());
                row1.createCell(3).setCellValue(areaList.get(j).getDistrict());
                row1.createCell(4).setCellValue(areaList.get(j).getPostcode());
            }
        }
        try {
            //3.将excel文件写入客户端,提供文件下载
            String filename = "qysj.xlsx";
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


    /**
     * 分页查询
     * @return
     */
    @Action(value = "area_pageQuery",results = @Result(name = "success",type = "json"))
    public String pageQuery(){
        //查询数据
        Page<Area> pageData = areaService.findPageData(model,page,rows);
        // 压入值栈
        pushPageDataToValueStack(pageData);
        return SUCCESS;
    }
    /**
     * 查询所有地区
     * @return
     */
    @Action(value = "area_findAll",results = @Result(name = "success",type = "json"))
    public String findAll(){
        //查询数据
        List<Area> areas = areaService.findAll();
        // 压入值栈
        ActionContext.getContext().getValueStack().push(areas);
        return SUCCESS;
    }

    //属性驱动,接收删除数据的i的字符串
    public String ids;
    public void setIds(String ids) {
        this.ids = ids;
    }

    @Action(value = "area_delBetch",results = {
            @Result(name = "success",type = "redirect",location = "./pages/base/area.html")})
    public String delBetch(){
        // 按,分隔ids
        String[] idArray = ids.split(",");
        // 调用业务层，批量作废
        areaService.delBatch(idArray);
        return SUCCESS;
    }
}
