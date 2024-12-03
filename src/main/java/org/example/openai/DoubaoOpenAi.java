package org.example.openai;

import com.volcengine.ark.runtime.model.completion.chat.*;
import com.volcengine.ark.runtime.service.ArkService;

import java.util.ArrayList;
import java.util.List;
/**
 * @description 字节大模型豆包接口
 */
public class DoubaoOpenAi extends BaseOpenAi{
    private String key;
    private String code;
    public DoubaoOpenAi(String key, String code, String name) {
        this.key = key;
        this.code = code;
        this.setModelName(name);
    }
    @Override
    public String doSendChatMessages(List<String> prompts) throws InterruptedException {
        int maxRetries = 3; // 设置最大重试次数
        int retryCount = 0;

        String apiKey = key;
        ArkService service = ArkService.builder().apiKey(apiKey).baseUrl("https://ark.cn-beijing.volces.com/api/v3/").build();

        final List<ChatMessage> messages = new ArrayList<>();
        final ChatMessage systemMessage = ChatMessage.builder().role(ChatMessageRole.SYSTEM).content(prompts.get(0)).build();
        final ChatMessage userMessage = ChatMessage.builder().role(ChatMessageRole.USER).content(prompts.get(1)).build();
        messages.add(systemMessage);
        messages.add(userMessage);

        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(code)
                .messages(messages)
                .build();
        while (retryCount < maxRetries) {
            try{
                ChatCompletionResult chatCompletion = service.createChatCompletion(chatCompletionRequest);
                if(!chatCompletion.getChoices().isEmpty()) {
                    List<ChatCompletionChoice> choices = chatCompletion.getChoices();
                    service.shutdownExecutor();
                    return (String) choices.get(0).getMessage().getContent();
                }
                System.out.println("豆包大模型请求失败，响应码非200或相应存在问题，等待10秒后重试...");
                Thread.sleep(10000); // 线程休眠10秒
                retryCount++;
            }catch(Exception e){
                e.printStackTrace();
                System.out.println("豆包大模型请求失败，，等待10秒后重试...");
                Thread.sleep(10000); // 线程休眠10秒
                retryCount++;
            }
        }
        return null;
    }
}
