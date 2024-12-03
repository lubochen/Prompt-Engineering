package org.example.openai.impl;

import com.google.gson.Gson;
import org.example.openai.BaseOpenAi;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @description 阿里大模型接口
 */
public class AliOpenAi extends BaseOpenAi {
    private String key;
    private String code;

    public AliOpenAi(String key, String code, String name) {
        this.key = key;
        this.code = code;
        this.setModelName(name);
    }
    @Override
    public String doSendChatMessages(List<String> prompts) {
        int maxRetries = 3;
        int retryCount = 0;
        while (retryCount < maxRetries) {
            try {
                RequestBody requestBody = new RequestBody(
                        code,
                        new Message[] {
                                new Message("system", prompts.get(0)),
                                new Message("user", prompts.get(1))
                        }
                );

                // 将请求体转换为 JSON
                Gson gson = new Gson();
                String jsonInputString = gson.toJson(requestBody);

                // 创建 URL 对象
                URL url = new URL("https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                // 设置请求方法为 POST
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type", "application/json; utf-8");
                httpURLConnection.setRequestProperty("Accept", "application/json");

                String apiKey = key;
                String auth = "Bearer " + apiKey;
                httpURLConnection.setRequestProperty("Authorization", auth);

                // 启用输入输出流
                httpURLConnection.setDoOutput(true);

                // 写入请求体
                try (OutputStream os = httpURLConnection.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                // 获取响应码
                int responseCode = httpURLConnection.getResponseCode();
                if (responseCode == 200) {
                    // 读取响应体
                    try (BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), StandardCharsets.UTF_8))) {
                        StringBuilder response = new StringBuilder();
                        String responseLine;
                        while ((responseLine = br.readLine()) != null) {
                            response.append(responseLine.trim());
                        }
                        ResponseModel responseModel = gson.fromJson(response.toString(), ResponseModel.class);
                        if (responseModel!= null && responseModel.getChoices()!= null ) {
                            String content = responseModel.getChoices()[0].getMessage().getContent();
                            return content;
                        }
                    }
                }
                // 响应码不等于200时，等待10秒后重试
                System.out.println("通义千问请求失败，响应码非200或相应存在问题，等待10秒后重试...");
                Thread.sleep(10000); // 线程休眠10秒
                retryCount++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    static class Message {
        String role;
        String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }
    static class RequestBody {
        String model;
        Message[] messages;

        public RequestBody(String model, Message[] messages) {
            this.model = model;
            this.messages = messages;
        }
    }

    static class ResponseModel {
        private Choice[] choices;

        public Choice[] getChoices() {
            return choices;
        }

        private static class Choice {
            private Message message;
            public Message getMessage() {
                return message;
            }

            private static class Message {
                private String content;
                public String getContent() {
                    return content;
                }
            }
        }
    }
}
