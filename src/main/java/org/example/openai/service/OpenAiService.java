package org.example.openai.service;

import org.example.constant.Model;
import org.example.excel.entity.ExcelDataRow;
import org.example.excel.ExcelWriter;
import org.example.excel.entity.ExportExcelDataRow;
import org.example.openai.OpenAi;
import org.example.openai.impl.AliOpenAi;
import org.example.openai.impl.ChatglmOpenAi;
import org.example.openai.impl.DoubaoOpenAi;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @description 大模型测试接口
 */
public class OpenAiService {
    static String doubaoKey;
    static String aliKey;
    static String zhipuKey;
    static String prompt;
    public static void loadKey(){
        // 创建Properties对象
        Properties properties = new Properties();
        // 加载配置文件，文件路径根据实际情况修改
        try {
            InputStreamReader reader = new InputStreamReader(OpenAiService.class.getClassLoader().getResourceAsStream("application.properties"), StandardCharsets.UTF_8);
            properties.load(reader);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // 根据键获取值
        doubaoKey = properties.getProperty("doubaoKey");
        aliKey = properties.getProperty("aliKey");
        zhipuKey = properties.getProperty("zhipuKey");
        prompt = properties.getProperty("prompt");
    }
    public static void test(List<ExcelDataRow> dataList){
        loadKey();

        List<OpenAi> openAiList = Stream.of(
                new AliOpenAi(aliKey, Model.QWEN_PLUS.getCode(), Model.QWEN_PLUS.getName()),
                new AliOpenAi(aliKey, Model.QWEN_TURBO.getCode(), Model.QWEN_TURBO.getName()),
                new DoubaoOpenAi(doubaoKey, Model.DOUBAO_PRO.getCode(), Model.DOUBAO_PRO.getName()),
                new DoubaoOpenAi(doubaoKey, Model.DOUBAO_LITE.getCode(), Model.DOUBAO_LITE.getName()),
                new ChatglmOpenAi(zhipuKey, Model.GLM_4.getCode(), Model.GLM_4.getName())
        ).collect(Collectors.toList());

        ExecutorService executorService = Executors.newFixedThreadPool(openAiList.size());

        for(OpenAi openAi: openAiList){
            Runnable runnable = () -> {
                List<ExportExcelDataRow> exportData = new ArrayList<>();
                long startTime = System.currentTimeMillis();
                for (ExcelDataRow data : dataList) {
                    String result = null;
                    try {
                        result = openAi.sendMessage(Arrays.asList(prompt, data.getPrompt()));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    if (result.startsWith("TP") || result.startsWith("FP")) {
                        if (result.startsWith(data.getFinalLabel()) && data.getFinalLabel().equals("TP")) {
                            openAi.addTruePositive();
                        }
                        if (result.startsWith(data.getFinalLabel()) && data.getFinalLabel().equals("FP")) {
                            openAi.addTrueNegative();
                        }
                        if (!result.startsWith(data.getFinalLabel()) && data.getFinalLabel().equals("TP")) {
                            openAi.addFalseNegative();
                        }
                        if (!result.startsWith(data.getFinalLabel()) && data.getFinalLabel().equals("FP")) {
                            openAi.addFalsePositive();
                        }
                        exportData.add(ExportExcelDataRow.builder()
                                .index(openAi.getTotalCount())
                                .before_index(data.getIndex())
                                .finalLabel(data.getFinalLabel())
                                .predictionLabel(result)
                                .build());
                        openAi.addTotalCount();
                    }
                    if(openAi.getTotalCount() % 100 == 0){
                        System.out.println( openAi.getModelName() + "已完成 = " + openAi.getTotalCount());
                    }
                }
                long endTime = System.currentTimeMillis();
                long duration = endTime - startTime;  // 计算任务运行时长
                // 输出各项指标
                System.out.println("------------------------------------------------------------------------");
                System.out.println("模型名称 = " + openAi.getModelName() + "已完成测试，测试数据如下：");
                System.out.println("准确率（Accuracy）: " + openAi.calculateAccuracy());
                System.out.println("召回率（Recall）: " + openAi.calculateRecall());
                System.out.println("精度（Precision）: " + openAi.calculatePrecision());
                System.out.println("F1分数（F1-Score）: " + openAi.calculateF1Score());
                System.out.println("任务运行时长：" + duration/1000 + "秒");
                System.out.println();
                System.out.println();
                System.out.println();
                ExcelWriter.writer(exportData, openAi.getModelName()+"_"+prompt);
            };
            executorService.submit(runnable);
        }
        // 关闭线程池，不再接受新任务，但会等待已提交任务执行完成
        executorService.shutdown();

        try {
            // 等待所有任务完成
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("所有任务已完成");
    }
}
