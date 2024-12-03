package org.example.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
/**
 * @description Excel数据源读取类
 */
//Excel 解析数据源
@Data
public class ExcelDataRow {

    @ExcelProperty("index")
    private Integer index;

    @ExcelProperty("category")
    private String category;

    @ExcelProperty("vtype")
    private String vtype;

    @ExcelProperty("final_label")
    private String finalLabel;

    @ExcelProperty("project")
    private String project;

    @ExcelProperty("warning_line")
    private String warningLine;

    @ExcelProperty("warning_method")
    private String warningMethod;

    public String getPrompt(){
        return "warning_line: " +  warningLine + "\n" +"warning_method: " + warningMethod;
    }
}