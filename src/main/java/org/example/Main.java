package org.example;

import org.example.excel.entity.ExcelDataRow;
import org.example.excel.ExcelParser;
import org.example.openai.service.OpenAiService;

import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        // 读取excel文件
        List<ExcelDataRow> dataList = ExcelParser.parse("test.xlsx");
        List<ExcelDataRow> firstTenList = dataList.stream()
                .limit(10)
                .collect(Collectors.toList());
        // 进行测试
        OpenAiService.test(firstTenList);
    }
}
