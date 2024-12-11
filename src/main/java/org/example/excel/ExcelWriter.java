package org.example.excel;

import com.alibaba.excel.EasyExcel;
import org.example.excel.entity.ExportExcelDataRow;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ExcelWriter {
    public static void writer(List<ExportExcelDataRow> exportData, String modelName) {
        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();
        // 定义时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HHmm");
        // 按照指定格式格式化时间
        String formattedTime = now.format(formatter);
        // 文件名
        String fileName = "src/main/resources/" + formattedTime +'-'+ modelName +"-result";
        // 文件名过长时截取
        if (fileName.length() > 180) {
            fileName = fileName.substring(0, 180);
        }
        fileName += ".xlsx";
        // 使用EasyExcel写入数据到Excel文件
        EasyExcel.write(fileName, ExportExcelDataRow.class).sheet("Sheet1").doWrite(exportData);

        System.out.println("数据已成功写入Excel文件！");
    }
}