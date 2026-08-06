# Cushion Auto-Interact

一个纯客户端 Fabric 模组：当**准心对准 `cushion` 命名空间的实体**时，自动以配置的间隔执行**使用键（默认右键）**交互，可用于自动骑乘 / 交互等场景。

## 支持版本

- Minecraft：**26.3 开发周期全部版本**（alpha / snapshot / 未来正式版，在 `fabric.mod.json` 中声明为 `>=26.3-alpha.1 <26.4`）
- Fabric Loader：`>=0.19.3`
- Fabric API：`*`
- Java：`>=25`

## 安装

1. 安装 [Fabric Loader](https://fabricmc.net/use/) 与 [Fabric API](https://modrinth.com/mod/fabric-api)（版本需匹配上面的支持范围）。
2. 将 `cushion-1.0.0.jar` 放入 `.minecraft/mods/`。
3. 启动游戏。

## 使用

- 默认**开启**自动交互（可用配置修改默认状态）。
- 按 **B 键**（可在 设置 → 控制 → 键位 中修改）随时切换自动交互的开关，切换时会在聊天栏提示当前状态。
- 功能开启时，只要准心对准目标实体且未打开任何界面，模组就会按配置的间隔自动执行使用键交互（等价于反复点击右键，结果由服务器裁决）。

## 配置

配置文件位于 `config/cushion.json`，首次启动时自动生成默认配置：

```json
{
  "enabledByDefault": true,
  "clickIntervalMs": 100,
  "targetEntities": [
    "minecraft:cushion"
  ]
}
```

| 字段 | 类型 | 默认值 | 说明 |
| --- | --- | --- | --- |
| `enabledByDefault` | `boolean` | `true` | 进入游戏时功能是否默认开启 |
| `clickIntervalMs` | `long` | `100` | 自动点击使用键的最小间隔（毫秒），越小点击越频繁 |
| `targetEntities` | `string[]` | `["minecraft:cushion"]` | 要自动点击的实体 ID 列表（见下方匹配规则） |

### 实体匹配规则

- `minecraft:cushion` —— 精确匹配 `minecraft` 命名空间下 ID 为 `cushion` 的实体（默认值）。
- `cushion:*` —— 匹配 `cushion` 命名空间下的**所有**实体。
- `minecraft:villager` —— 精确匹配指定 ID 的实体。
- `*` —— 匹配**所有**实体。
- 列表为空（`[]`）时，回退为匹配所有 `cushion:` 命名空间的实体。

## 构建

需要 JDK 25 与网络环境：

```powershell
$env:JAVA_HOME = "你的JDK25路径"
.\gradlew.bat build
```

产物位于 `build/libs/cushion-1.0.0.jar`。

## 许可

MIT License，详见 [LICENSE](LICENSE)。
