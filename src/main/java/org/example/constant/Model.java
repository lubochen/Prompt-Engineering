package org.example.constant;

/**
 * @description 大模型数据枚举类
 */
public enum Model {
    GLM_4("glm-4-flash","超快推理速度","glm-4-flash"),
    QWEN_PLUS("qwen-plus","能力均衡，推理效果、成本和速度介于通义千问-Max和通义千问-Turbo之间，适合中等复杂任务。","qwen-plus"),
    QWEN_TURBO("qwen-turbo","通义千问-Turbo，是通义千问系列中推理速度最快的模型，适合推理任务。","qwen-turbo"),
//    QWEN_CODER_PLUS("qwen-coder-plus","通义千问代码模型具有强大的代码能力,代码相关的任务或在复杂场景下的任务效果较好","qwen-coder-plus"),
    DOUBAO_PRO("ep-20241202215503-fgwpk","Doubao-pro-32k,是豆包推出行业领先的专业版大模型。模型在参考问答、摘要总结、创作等广泛的应用场景上能提供优质的回答，是同时具备高质量与低成本的极具性价比模型。","Doubao-pro-32k"),
    DOUBAO_LITE("ep-20241203130259-2vbjf","Doubao-lite-32k,拥有极致的响应速度，更好的性价比","Doubao-lite-32k");
    private final String code;
    private final String info;
    private final String name;

    Model(String code, String info, String name) {
        this.code = code;
        this.info = info;
        this.name = name;
    }
    public String getCode() {
        return code;
    }
    public String getInfo() {
        return info;
    }
    public String getName() {
        return name;
    }
}
