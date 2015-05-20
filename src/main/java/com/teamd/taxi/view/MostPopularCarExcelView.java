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


public class MostPopularCarExcelView extends AbstractExcelView{
    @Override
    protected void buildExcelDocument(Map model, HSSFWorkbook workbook,
                                      HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-disposition", "attachment; filename=MostPopularCar.xls");
        HSSFSheet excelSheet = workbook.createSheet("Most popular car");
        setExcelHeader(excelSheet,workbook);
        excelSheet.setDefaultColumnWidth(30);
        List report = (List) model.get("report");
        setExcelRows(excelSheet,report);

    }

    public void setExcelHeader(HSSFSheet excelSheet,HSSFWorkbook workbook) {
        HSSFRow excelHeader = excelSheet.createRow(0);
        excelHeader.createCell(0).setCellValue("Car model");
        excelHeader.createCell(1).setCellValue("Car class");
        excelHeader.createCell(2).setCellValue("Count of usage");
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("Arial");
        style.setFillForegroundColor(HSSFColor.BLUE.index);
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        font.setColor(HSSFColor.WHITE.index);
        style.setFont(font);
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
            excelRow.createCell(2).setCellValue(report.getField3());
        }
    }
}
