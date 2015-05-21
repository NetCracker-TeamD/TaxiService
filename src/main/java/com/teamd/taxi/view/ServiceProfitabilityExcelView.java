package com.teamd.taxi.view;

import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.springframework.web.servlet.view.document.AbstractExcelView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public class ServiceProfitabilityExcelView extends AbstractExcelView {
    @Override
    protected void buildExcelDocument(Map model, HSSFWorkbook workbook,
                                      HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-disposition", "attachment; filename=ServiceProfitabilityByMonth.xls");
        HSSFSheet excelSheet = workbook.createSheet("Service profitability by month");
        setExcelHeader(excelSheet,workbook);
        excelSheet.setDefaultColumnWidth(30);
        List report = (List) model.get("report");
        setExcelRows(excelSheet,report);


    }

    public void setExcelHeader(HSSFSheet excelSheet,HSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("Arial");
        style.setFillForegroundColor(HSSFColor.BLUE.index);
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        font.setColor(HSSFColor.WHITE.index);
        style.setFont(font);
        HSSFRow excelHeader = excelSheet.createRow(0);
        excelHeader.createCell(0).setCellValue("Period of time");
        excelHeader.createCell(1).setCellValue("Profit");
        for (Cell cell:excelHeader){
            cell.setCellStyle(style);
        }
    }

    public void setExcelRows(HSSFSheet excelSheet, List<Report> reports){
        int record = 1;
        for (Report report : reports) {
            HSSFRow excelRow = excelSheet.createRow(record++);
            excelRow.createCell(0).setCellValue(report.getField1());
            excelRow.createCell(1).setCellValue(report.getField2());
        }
    }
}
