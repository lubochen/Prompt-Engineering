在 `application.properties` 中，存储密钥及模型 `prompt` ：

- `aliKey`：通义千问 `Key`
- `doubaoKey`：豆包 `Key`
- `zhipuKey`：智谱 `AI Key`

在 `org.example.openai.service.OpenAiService` 类的 `#test` 方法内设置测试所需模型，随后，启动 `org.example.Main#main` 方法，便可开启测试流程。测试完成后，原始结果 (.xlsx格式) 将自动保存在 `resources` 目录下。
