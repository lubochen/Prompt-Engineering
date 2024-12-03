package org.example.openai;

import java.util.List;

/**
 * @description 大模型接口
 */
public interface OpenAi {
    String sendMessage(List<String> prompts);

    void addTruePositive();

    void addTrueNegative();

    void addFalsePositive();

    void addFalseNegative();

    void addTotalCount();

    String calculateAccuracy();

    String calculateRecall();

    String calculatePrecision();

    String calculateF1Score();

    String getModelName();
}
