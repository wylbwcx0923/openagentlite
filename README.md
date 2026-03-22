# OpenAgentLite

基于JAVA语言纯原生开发的OpenClaw系统OpenAgentLite基于 ReAct 框架的智能代理系统，支持工具调用、技能管理、多模型切换和记忆功能。

## 技术栈

- Spring Boot 3.5.4
- Java 19
- OkHttp 4.9.3
- Fastjson 2.0.33
- FreeMarker（模板引擎）
- SLF4J（日志框架）

## 项目结构

```
openagentlite/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── open/
│   │   │           └── agent/
│   │   │               └── lite/
│   │   │                   ├── controller/     # API控制器
│   │   │                   ├── core/           # 核心组件
│   │   │                   │   ├── ability/     # 核心能力模块
│   │   │                   │   ├── engine/     # ReAct引擎
│   │   │                   │   ├── memory/     # 记忆模块
│   │   │                   │   ├── parser/     # 响应解析器
│   │   │                   │   ├── prompt/     # 提示词管理
│   │   │                   │   ├── scanner/    # 组件扫描器
│   │   │                   │   └── task/       # 任务管理
│   │   │                   ├── llm/            # 大模型客户端
│   │   │                   ├── mcp/            # 工具调度器
│   │   │                   │   └── tool/       # 工具系统
│   │   │                   ├── skill/          # 技能系统
│   │   │                   │   ├── impl/       # 技能实现
│   │   │                   │   └── loader/     # 技能加载器
│   │   │                   └── OpenAgentLiteApplication.java
│   │   └── resources/
│   │       ├── prompts/       # 提示词模板
│   │       ├── skills/        # 技能定义文件
│   │       └── application.yml # 配置文件
│   └── test/                  # 测试代码
├── .gitignore
├── pom.xml
└── README.md
```

## 核心功能

### 1. ReAct 引擎
- 实现了 ReAct（Reasoning + Acting）框架
- 支持思考、行动、观察的循环过程
- 基于枚举驱动的流程控制
- 支持超时和最大步数限制

### 2. 核心能力模块
- **FileAbility**: 文件读写能力，支持文件的读取、写入、删除和检查
- **BrowserAbility**: 浏览器操作能力，支持打开浏览器和访问网页
- **ShellAbility**: Shell执行能力，支持执行Shell脚本和命令
- **AppAbility**: 应用启动能力，支持启动应用程序
- **CodeExecuteAbility**: 代码执行能力，支持执行Java代码
- **NetworkAbility**: 网络请求能力，支持网络请求和搜索功能
- **CalculatorAbility**: 数学计算能力，支持基本的数学运算

### 3. 技能系统
- 基于SKILL.md文件的技能定义
- 技能自动加载和注册
- 天气查询技能示例
- 可自定义技能

### 4. 工具系统
- 网络搜索工具
- 文件操作工具
- 计算器工具
- 可扩展的工具接口

### 5. 多模型支持
- 基于工厂模式的模型切换
- 支持多种LLM模型
- 可扩展的模型接口

### 6. 记忆模块
- 短期记忆（内存存储）
- 长期记忆（文件存储）
- 记忆压缩和概括
- 相关记忆检索

### 7. 提示词管理
- 基于模板引擎的提示词生成
- 可配置的提示词模板
- 动态提示词拼接

### 8. 自动扫描机制
- 系统启动时自动扫描所有Skill、Tool和Ability组件
- 自动注册扫描到的组件
- 减少人工配置成本

### 9. 企业级规范
- 严格遵循统一代码规范
- 高内聚、低耦合的代码结构
- 完善的项目文档

## 快速开始

### 1. 配置环境

1. 确保安装了 Java 19 或更高版本
2. 确保安装了 Maven 3.6 或更高版本

### 2. 配置 LLM 模型

编辑 `src/main/resources/application.yml` 文件，配置 LLM 模型参数：

```yaml
llm:
  qwen:
    api-key: "your-api-key"
    api-url: "https://api.example.com/v1/chat/completions"
    model: "qwen-turbo"
```

### 3. 创建自定义技能

在 `src/main/resources/skills` 目录下创建技能目录和 SKILL.md 文件：

```markdown
# 技能名称
## 基础信息
- 唯一标识(skillId)：skill_name
- 归属分类：系统操作 / 文件处理 / 数据查询 / 工具调用 / 业务能力
- 版本：v1.0.0
- 作者：项目团队
- 描述：一句话清晰说明该 Skill 能做什么

## 功能说明
详细描述 Skill 用途、适用场景、解决的问题。

## 输入参数
| 参数名 | 类型 | 是否必填 | 描述 | 示例 |
|--------|------|----------|------|------|
| param1 | string | 是 | 参数说明 | 示例值 |
| param2 | int | 否 | 参数说明 | 示例值 |

## 输出结果
- 成功：返回格式、数据结构、示例
- 失败：错误码、错误信息、处理建议

## 依赖能力
- 文件读写能力（FileAbility）
- 浏览器操作能力（BrowserAbility）
- Shell 执行能力（ShellAbility）
- 应用启动能力（AppAbility）
- 代码执行能力（CodeExecuteAbility）

## 执行流程
1. 接收输入参数
2. 参数校验
3. 调用对应底层能力
4. 处理返回结果
5. 格式化输出

## 使用示例
### 调用指令示例
用户自然语言指令示例

### 参数示例
{
  "param1": "value",
  "param2": 123
}

### 返回结果示例
{
  "code": 200,
  "message": "执行成功",
  "data": {}
}

## 注意事项
- 执行权限说明
- 超时限制
- 异常处理规则
- 安全规范
```

### 4. 构建和运行

```bash
# 构建项目
mvn clean package

# 运行项目
mvn spring-boot:run
```

### 5. 访问 API

项目运行后，API 接口地址为：`http://localhost:8888/api`

## API 接口

### 1. 创建任务

**POST /api/tasks**

请求体：
```json
{
  "prompt": "What is the capital of France?"
}
```

响应：
```json
{
  "taskId": "uuid"
}
```

### 2. 执行任务

**POST /api/tasks/{taskId}/execute**

响应：
```json
{
  "result": "Paris"
}
```

### 3. 获取任务状态

**GET /api/tasks/{taskId}**

响应：
```json
{
  "id": "uuid",
  "prompt": "What is the capital of France?",
  "result": "Paris",
  "status": "COMPLETED",
  "createdAt": "2026-03-21T16:00:00",
  "updatedAt": "2026-03-21T16:00:01"
}
```

### 4. 获取任务历史

**GET /api/tasks**

响应：
```json
{
  "tasks": {
    "uuid": {
      "id": "uuid",
      "prompt": "What is the capital of France?",
      "result": "Paris",
      "status": "COMPLETED",
      "createdAt": "2026-03-21T16:00:00",
      "updatedAt": "2026-03-21T16:00:01"
    }
  }
}
```

### 5. 删除任务

**DELETE /api/tasks/{taskId}**

响应：
```json
{
  "success": true
}
```

## 示例

### 1. 基本对话

```bash
# 创建任务
curl -X POST http://localhost:8888/api/tasks \
  -H "Content-Type: application/json" \
  -d '{"prompt": "What is the capital of France?"}'

# 执行任务
curl -X POST http://localhost:8888/api/tasks/{taskId}/execute
```

### 2. 使用工具

```bash
# 创建任务
curl -X POST http://localhost:8888/api/tasks \
  -H "Content-Type: application/json" \
  -d '{"prompt": "Calculate 2 + 2"}'

# 执行任务
curl -X POST http://localhost:8888/api/tasks/{taskId}/execute
```

### 3. 使用技能

```bash
# 创建任务
curl -X POST http://localhost:8888/api/tasks \
  -H "Content-Type: application/json" \
  -d '{"prompt": "What is the weather in Beijing?"}'

# 执行任务
curl -X POST http://localhost:8888/api/tasks/{taskId}/execute
```

## 贡献指南

1. Fork 项目
2. 创建功能分支
3. 提交更改
4. 推送到分支
5. 创建 Pull Request

## 许可证

MIT License
