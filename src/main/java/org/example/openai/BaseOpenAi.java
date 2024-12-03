package org.example.openai;


import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

/**
 * @description 抽象类，存储当前模型的测试数据
 */
public abstract class BaseOpenAi implements OpenAi{

    private int TP = 0;
    private int TN = 0;
    private int FP = 0;
    private int FN = 0;
    private String modelName;
    private int total = 0;
    @Override
    public String sendMessage(List<String> messages) {
        try {
            return doSendChatMessages(messages);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
        }
    }
    protected abstract String doSendChatMessages(List<String> messages) throws IOException, InterruptedException;

    public void addTruePositive() {
        TP++;
    }

    // 增加真负例计数
    public void addTrueNegative() {
        TN++;
    }

    // 增加假正例计数
    public void addFalsePositive() {
        FP++;
    }

    // 增加假反例计数
    public void addFalseNegative() {
        FN++;
    }

    // 增加总样本数计数
    public void addTotalCount() {
        total++;
    }
    public Integer getTotalCount() {
        return total;
    }
    // 计算准确率（Accuracy），并格式化返回值保留四位小数
    public String calculateAccuracy() {
        double accuracy = (double) (TP + TN) / total;
        DecimalFormat decimalFormat = new DecimalFormat("#.####");
        return decimalFormat.format(accuracy);
    }

    // 计算召回率（Recall），并格式化返回值保留四位小数
    public String calculateRecall() {
        double recall = (double) TP / (TP + FN);
        DecimalFormat decimalFormat = new DecimalFormat("#.####");
        return decimalFormat.format(recall);
    }

    // 计算精度（Precision），并格式化返回值保留四位小数
    public String calculatePrecision() {
        double precision = (double) TP / (TP + FP);
        DecimalFormat decimalFormat = new DecimalFormat("#.####");
        return decimalFormat.format(precision);
    }

    // 计算F1分数（F1-Score），并格式化返回值保留四位小数
    public String calculateF1Score() {
        double precision = (double) TP / (TP + FP);
        double recall = (double) TP / (TP + FN);
        double f1Score = 2 * (precision * recall) / (precision + recall);
        DecimalFormat decimalFormat = new DecimalFormat("#.####");
        return decimalFormat.format(f1Score);
    }
    public String getModelName(){
        return modelName;
    }
    public void setModelName(String modelName){
        this.modelName = modelName;
    }


}
