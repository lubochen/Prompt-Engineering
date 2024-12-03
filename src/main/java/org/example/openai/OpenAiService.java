package org.example.openai;

import org.example.constant.Model;
import org.example.excel.ExcelDataRow;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
    public static void loadKey(){
        // 创建Properties对象
        Properties properties = new Properties();
        // 加载配置文件，文件路径根据实际情况修改
        try {
            InputStream inputStream = OpenAiService.class.getClassLoader().getResourceAsStream("application.properties");
            properties.load(inputStream);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // 根据键获取值
        doubaoKey = properties.getProperty("doubaoKey");
        aliKey = properties.getProperty("aliKey");
        zhipuKey = properties.getProperty("zhipuKey");
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
                long startTime = System.currentTimeMillis();
                for (ExcelDataRow data : dataList) {
                    String result = openAi.sendMessage(Arrays.asList("作为一名专业的代码专家，你只能回复TP或FP。分析给定的warning_line和warning_method,告诉我这个代码是否存在源码警告问题。如果存在源码警告问题，返回TP；若不存在，返回FP", data.getPrompt()));
                    if (result.equals("TP") || result.equals("FP")) {
                        if (result.equals(data.getFinalLabel()) && data.getFinalLabel().equals("TP")) {
                            openAi.addTruePositive();
                        }
                        if (result.equals(data.getFinalLabel()) && data.getFinalLabel().equals("FP")) {
                            openAi.addTrueNegative();
                        }
                        if (!result.equals(data.getFinalLabel()) && data.getFinalLabel().equals("TP")) {
                            openAi.addFalseNegative();
                        }
                        if (!result.equals(data.getFinalLabel()) && data.getFinalLabel().equals("FP")) {
                            openAi.addFalsePositive();
                        }
                        openAi.addTotalCount();
                    }
                }
                long endTime = System.currentTimeMillis();
                long duration = endTime - startTime;  // 计算任务运行时长
                // 输出各项指标
                System.out.println("------------------------------------------------------------------------");
                System.out.println("模型名称 = " + openAi.getModelName() + "已完成训练，训练数据如下：");
                System.out.println("准确率（Accuracy）: " + openAi.calculateAccuracy());
                System.out.println("召回率（Recall）: " + openAi.calculateRecall());
                System.out.println("精度（Precision）: " + openAi.calculatePrecision());
                System.out.println("F1分数（F1-Score）: " + openAi.calculateF1Score());
                System.out.println("任务运行时长：" + duration/1000 + "秒");
                System.out.println();
                System.out.println();
                System.out.println();
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
