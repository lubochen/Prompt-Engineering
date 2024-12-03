package org.example.excel;

import com.alibaba.excel.EasyExcel;

import java.io.File;
import java.net.URL;
import java.util.List;

/**
 * @description Excel数据源解析类
 */
public class ExcelParser {
    public static List<ExcelDataRow>  parse(String fileName) {
        // 获取当前类的ClassLoader
        ClassLoader classLoader = ExcelParser.class.getClassLoader();
        // 通过ClassLoader获取Excel文件的URL
        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new RuntimeException("Excel文件未找到");
        }
        File excelFile;
        try {
            // 将URL转换为File对象
            excelFile = new File(resource.toURI());
        } catch (java.net.URISyntaxException e) {
            throw new RuntimeException("无法将Excel文件URL转换为File对象", e);
        }
        ExcelDataListener listener = new ExcelDataListener();
        EasyExcel.read(excelFile, ExcelDataRow.class, listener).sheet().doRead();
        List<ExcelDataRow> parsedData = listener.getDataList();
        return parsedData;
    }
}