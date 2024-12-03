package org.example.openai.impl;

import com.zhipu.oapi.ClientV4;
import com.zhipu.oapi.Constants;
import com.zhipu.oapi.service.v4.model.ChatCompletionRequest;
import com.zhipu.oapi.service.v4.model.ChatMessage;
import com.zhipu.oapi.service.v4.model.ChatMessageRole;
import com.zhipu.oapi.service.v4.model.ModelApiResponse;
import org.example.openai.BaseOpenAi;

import java.util.ArrayList;
import java.util.List;
/**
 * @description 智谱AI大模型接口
 */
public class ChatglmOpenAi extends BaseOpenAi {
    public ChatglmOpenAi(String key, String code, String name){
        this.client = new ClientV4.Builder(key).build();
        this.code = code;
        this.setModelName(name);
    }
    ClientV4 client;
    String code;
    @Override
    public String doSendChatMessages(List<String> prompts) throws InterruptedException {
        int maxRetries = 3; // 设置最大重试次数
        int retryCount = 0;
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage(ChatMessageRole.USER.value(), prompts.get(0)));
        messages.add(new ChatMessage(ChatMessageRole.USER.value(), prompts.get(1)));

        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(code)
                .stream(Boolean.FALSE)
                .invokeMethod(Constants.invokeMethod)
                .messages(messages)
                .build();
        while (retryCount < maxRetries) {
            ModelApiResponse invokeModelApiResp = client.invokeModelApi(chatCompletionRequest);
            int responseCode = invokeModelApiResp.getCode();
            if(responseCode==200){
                return (String) invokeModelApiResp.getData().getChoices().get(0).getMessage().getContent();
            }
            // 响应码不等于200时，等待10秒后重试
            System.out.println("智谱AI请求失败，响应码非200或相应存在问题，等待10秒后重试...");
            Thread.sleep(10000); // 线程休眠10秒
            retryCount++;
        }
        return null;
    }
}
