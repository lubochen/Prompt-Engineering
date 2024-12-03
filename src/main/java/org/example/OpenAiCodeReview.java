package org.example;

import org.example.excel.ExcelDataRow;
import org.example.excel.ExcelParser;
import org.example.openai.DoubaoOpenAi;
import org.example.openai.OpenAi;
import org.example.openai.OpenAiService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OpenAiCodeReview {
    public static void main(String[] args) {
        // 读取excel文件
        List<ExcelDataRow> dataList = ExcelParser.parse("test.xlsx");
        List<ExcelDataRow> firstTenList = dataList.stream()
                .limit(100)
                .collect(Collectors.toList());
        // 进行测试
        OpenAiService.test(firstTenList);
    }
}
