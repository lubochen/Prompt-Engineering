package org.example.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import org.example.excel.entity.ExcelDataRow;

import java.util.ArrayList;
import java.util.List;
/**
 * @description EasyExcel读取
 */
public class ExcelDataListener extends AnalysisEventListener<ExcelDataRow> {
    private List<ExcelDataRow> dataList = new ArrayList<>();

    @Override
    public void invoke(ExcelDataRow dataRow, AnalysisContext context) {
        dataList.add(dataRow);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        System.out.println("解析完成，共解析到 " + dataList.size() + " 行数据");
    }

    public List<ExcelDataRow> getDataList() {
        return dataList;
    }
}