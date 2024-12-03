package org.example.excel.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description Excel数据源导出实体类
 */
//导出Excel 数据源
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExportExcelDataRow {

    @ExcelProperty("index")
    private Integer index;

    @ExcelProperty("before_index")
    private Integer before_index;

    @ExcelProperty("final_label")
    private String finalLabel;

    @ExcelProperty("prediction_label")
    private String predictionLabel;

}