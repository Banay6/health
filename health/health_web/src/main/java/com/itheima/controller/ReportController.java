package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.entity.Result;
import com.itheima.service.MemberService;
import com.itheima.service.PackageService;
import com.itheima.service.ReportService;
import com.itheima.utils.DateUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/report")
@PreAuthorize("hasAnyAuthority('REPORT_VIEW')")
public class ReportController {
    @Reference
    private MemberService memberService;
    @Reference
    private PackageService packageService;
    @Reference
    private ReportService reportService;

    @GetMapping("/getMemberReport")
    public Result getMemberReport() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -12);

        List<String> list = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            calendar.add(Calendar.MONTH, 1);
            list.add(new SimpleDateFormat("yyyy.MM").format(calendar.getTime()));
        }

        Map<String, Object> map = new HashMap<>();
        map.put("months", list);
        List<Integer> memberCount = memberService.findMemberCountByMonth(list);
        map.put("memberCount", memberCount);

        return new Result(true, MessageConstant.GET_MEMBER_NUMBER_REPORT_SUCCESS, map);
    }

    @RequestMapping("/findMemberCountBy2Month.do")
    public Result findMemberCountBy2Month(String startMonth, String endMonth) {
        try {
            String startDate = startMonth + "-01";
            String endDate = endMonth + "-31";
            //用工具类得到中间月份
            List<String> months = DateUtils.getMonthBetween(startDate, endDate, "yyyy-MM");
            List<Integer> list = memberService.findMemberCountByMonth(months);

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("months", months);
            map.put("memberCount", list);
            return new Result(true, MessageConstant.GET_MEMBER_NUMBER_REPORT_SUCCESS, map);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.GET_MEMBER_NUMBER_REPORT_FAIL);
        }
    }

    @GetMapping("/getPackageReport")
    public Result getPackageReport() {
        List<Map<String, Object>> list = packageService.findPackageCount();

        Map<String, Object> map = new HashMap<>();
        map.put("packageCount", list);

        List<String> packageNames = new ArrayList<>();
        for (Map<String, Object> m : list) {
            String name = (String) m.get("name");
            packageNames.add(name);

        }
        map.put("packageNames", packageNames);

        return new Result(true, MessageConstant.GET_PACKAGE_COUNT_REPORT_SUCCESS, map);
    }

    @GetMapping("/getBusinessReportData")
    public Result getBusinessReportData() {
        Map<String, Object> result = null;
        try {
            result = reportService.getBusinessReport();
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(true, MessageConstant.GET_BUSINESS_REPORT_FAIL);
        }
        return new Result(true, MessageConstant.GET_BUSINESS_REPORT_SUCCESS, result);
    }

    @RequestMapping("/exportBusinessReport")
    public Result exportBusinessReport(HttpServletRequest request, HttpServletResponse response) {
        try {
            Map<String, Object> result = reportService.getBusinessReport();
            //取出返回结果数据，准备将报表数据写入到Excel文件中
            String reportDate = (String) result.get("reportDate");
            Integer todayNewMember = (Integer) result.get("todayNewMember");
            Integer totalMember = (Integer) result.get("totalMember");
            Integer thisWeekNewMember = (Integer) result.get("thisWeekNewMember");
            Integer thisMonthNewMember = (Integer) result.get("thisMonthNewMember");
            Integer todayOrderNumber = (Integer) result.get("todayOrderNumber");
            Integer thisWeekOrderNumber = (Integer) result.get("thisWeekOrderNumber");
            Integer thisMonthOrderNumber = (Integer) result.get("thisMonthOrderNumber");
            Integer todayVisitsNumber = (Integer) result.get("todayVisitsNumber");
            Integer thisWeekVisitsNumber = (Integer) result.get("thisWeekVisitsNumber");
            Integer thisMonthVisitsNumber = (Integer) result.get("thisMonthVisitsNumber");
            List<Map> hotPackage = (List<Map>) result.get("hotPackage");

            String temlateRealPath = request.getSession().getServletContext().getRealPath("template") + File.separator + "report_template.xlsx";

            //读取模板文件创建Excel表格对象
            XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(new File(temlateRealPath)));
            XSSFSheet sheet = workbook.getSheetAt(0);

            XSSFRow row = sheet.getRow(2);
            row.getCell(5).setCellValue(reportDate);

            row = sheet.getRow(4);
            row.getCell(5).setCellValue(todayNewMember);//新增会员数（本日）
            row.getCell(7).setCellValue(totalMember);//总会员数

            row = sheet.getRow(5);
            row.getCell(5).setCellValue(thisWeekNewMember);//本周新增会员数
            row.getCell(7).setCellValue(thisMonthNewMember);//本月新增会员数

            row = sheet.getRow(7);
            row.getCell(5).setCellValue(todayOrderNumber);//今日预约数
            row.getCell(7).setCellValue(todayVisitsNumber);//今日到诊数

            row = sheet.getRow(8);
            row.getCell(5).setCellValue(thisWeekOrderNumber);//本周预约数
            row.getCell(7).setCellValue(thisWeekVisitsNumber);//本周到诊数

            row = sheet.getRow(9);
            row.getCell(5).setCellValue(thisMonthOrderNumber);//本月预约数
            row.getCell(7).setCellValue(thisMonthVisitsNumber);//本月到诊数

            int rowNum = 12;
            for (Map map : hotPackage) {//热门套餐
                String name = (String) map.get("name");
                Long package_count = (Long) map.get("package_count");
                BigDecimal proportion = (BigDecimal) map.get("proportion");
                row = sheet.getRow(rowNum++);
                row.getCell(4).setCellValue(name);//套餐名称
                row.getCell(5).setCellValue(package_count);//预约数量
                row.getCell(6).setCellValue(proportion.doubleValue());//占比
            }
            //通过输出流进行文件下载
            ServletOutputStream out = response.getOutputStream();
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("content-Disposition", "attachment;filename=report.xlsx");
            workbook.write(out);

            out.flush();
            out.close();
            workbook.close();

            return null;


        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.GET_BUSINESS_REPORT_FAIL, null);
        }
    }

    /**
     * 性别分析
     *
     * @return
     */
    @GetMapping("/getGender")
    public Result getGender() {
        List<Map<String, Object>> genderCount = reportService.getGender();
        List<String> genderNames = new ArrayList<>();
        for (Map<String, Object> map : genderCount) {
            genderNames.add((String) map.get("name"));
        }
        Map<String, Object> map = new HashMap<>();
        map.put("genderNames", genderNames);
        map.put("genderCount", genderCount);
        return new Result(true, MessageConstant.GET_PACKAGE_COUNT_REPORT_SUCCESS, map);
    }

    /**
     * 年龄分析
     *
     * @return
     */
    @GetMapping("/getAge")
    public Result getAge() {
        List<Map<String, Object>> ageData = reportService.getAge();
        List<String> ageName = new ArrayList<>();
        for (Map<String, Object> map : ageData) {
            ageName.add((String) map.get("name"));
        }
        Map<String, Object> map = new HashMap<>();
        map.put("ageName", ageName);
        map.put("ageData", ageData);
        return new Result(true, MessageConstant.GET_PACKAGE_COUNT_REPORT_SUCCESS, map);
    }
}
