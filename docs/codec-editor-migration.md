# Codec 编辑器可靠性修复迁移说明

本次以 HTLib `1.20.1` 分支的 `ac8e40b`（1.3.0）为基线，核对独立 In-Game-Datapack-Editor 工程 `ed7eefd` 中的修复。编辑器已经合入 HTLib，采用新的控件树、缓存和服务端校验流程，因此按具体问题适配，不整包替换旧实现。

## 保留的新架构

- `CodecHelper` 发现数据包注册表，`common/codec/parse` 将 Codec 转成 Schema。
- `client/gui/screen/codec/node` 保存编辑数据，`client/gui/widget/codec` 持有控件；滚动和平移通过重新布局保留输入焦点。
- `EntrySchemaCache`、`RegistryEntriesCache` 支持条目结构与注册表候选，Holder 已有引用和内联定义的编辑方式。
- 编辑器已有 JSON 导入、服务端校验和 `EditorResultPacket` 反馈。命令入口为 `/htlib ide editor` 与 `/htlib ide schema ...`，沿用 HTLib 的翻译键和网络通道。
- 网络 Schema 用 `$defs/$ref` 保留共享结构，客户端恢复为共享 JSON DAG；Union 分支和未启用的 Optional 按需创建表单，已有分支草稿继续保留。命令行完整 JSON 导出仍使用原有树格式。

## 适用性对照

| 核对项 | HTLib 基线状态 | 迁移处理 |
| --- | --- | --- |
| 固定本机 HTLib JAR 路径，缺少测试依赖 | HTLib 本身就是库；已有 Java 17 工具链、JUnit 和公开依赖仓库 | 不添加对自身的 CurseMaven 依赖，不搬用旧构建文件 |
| 独立 `CodecReflection` 和空 `MapLike` 兼容实现 | 已有 `ReflectionUtil` 与 `CodecHelper.emptyMapLike()` | 复用现有工具，不新增平行实现 |
| 基础 Codec 解析测试缺失 | 作者已覆盖 Record、List、Map、Enum、Holder、dispatch、包装与编解码往返 | 保留现有测试，新增上下文和导出回归用例 |
| 实测发现世界生成配置被误识别为 Holder 或枚举 | 类型识别会扫描整个内部对象图，甚至进入 Registry 条目，被嵌套引用污染 | 限制类型识别的来源，在配置结构和 Registry 实例处停止；保留真实引用类型、标签能力及单字段对象 |
| 类型修正后大型 Schema 完全展开，无法加载 | `configured_feature` 的完整 JSON 树约 117 MB，包含大量重复子结构 | 网络使用共享定义后降为 355,288 UTF-8 字节；客户端避免预建未选分支、递归哈希和深拷贝整个子图 |
| Schema 命令缺少当前世界的注册表上下文 | 命令仍未设置；条目请求只设置而不恢复 | 采用 `SchemaRegistryAccess.withAccess`，作用域结束或异常时恢复，线程之间不共享世界上下文 |
| `worldgen/...` Schema 导出失败及路径逃逸 | 仍只创建 `schema_output` 顶层目录 | 保留 `schema_output/<namespace>_<registry-path>.json` 格式，安全创建父目录，拒绝异常路径段与链接逃逸，失败时命令返回 0 |
| 类型查询错误提示指向旧命令 | 提示仍为 `/htlib schema types` | 改为 `/htlib ide schema types` |
| 新增或删除 Map 条目导致草稿丢失 | 新控件树已改用增量布局 | 保留作者实现，不恢复旧的整体重建方式 |
| Union 使用 `type` 覆盖外层字段名、切换分支清空草稿 | 数据节点仍保留旧行为 | 在新版数据节点中修复，继续由现有 Widget 显示 |
| 可选字段无法表达缺省 | 新版已有折叠与默认值显示，仍需区分“是否存在”和“是否展开” | 保留紧凑控件与默认值，单独管理字段存在状态 |
| 数值统一经 `double` 转换、Map 重复键被覆盖 | 数据节点仍需修正 | 精确解析整数，保留非法输入供修改，阻止重复键静默丢失 |
| 空字符串刷新丢失、Holder 内联表单依赖渲染帧同步 | 新版仍把空文本视为缺失，内联子树只由控件持有 | 保留合法空字符串；内联草稿归属数据节点，参与同步收集与递归校验，重建控件时保留草稿 |
| 工具栏空值、窄窗口按钮与长反馈 | 新版已有独立数据包名称和保存菜单 | 适配五个工具栏控件及结果显示，不恢复旧版固定布局 |
| 保存请求直接携带服务器路径 | 新版已有服务端 Codec 校验与反馈，但路径写入仍需约束 | 沿用新的请求与反馈流程，由服务端根据数据包名称、注册表和资源 ID 推导路径 |
| 网络长度与延迟回包 | 新版已有结果缓存，但请求未关联唯一 ID | 限定 JSON 的 UTF-8 字节预算、校验编辑权限及消息方向，并用请求 UUID 匹配保存/校验结果 |

## 保存与兼容性

新版保留独立的数据包名称输入。例如数据包名称为 `editor_test`、资源 ID 为 `demo:sample`、注册表为 `minecraft:damage_type` 时，保存到世界目录下的 `datapacks/editor_test/data/demo/damage_type/sample.json`。非原版注册表在注册表路径前保留其命名空间，例如 `htlib:wave` 对应 `data/demo/htlib/wave/sample.json`。

服务端用目标 Codec 与完整 `RegistryOps` 校验 JSON，通过后才写文件；文件写入限定在世界数据包目录，采用临时文件与原子替换。仅在缺少 `pack.mcmeta` 时创建格式 15 的元数据，不覆盖已有元数据。保存成功表示文件已写入，数据包启用、重载或重新进入世界仍需按注册表的加载时机操作。

单次 JSON 请求上限为 30,000 个 UTF-8 字节，给 Minecraft 1.20.1 的 32,767 字节服务端自定义消息上限预留三个标识符、请求 UUID 与长度字段的空间。HTLib 网络通道协议更新为 `2.0`，握手要求版本完全一致；旧 `1.0` 客户端或服务端会被拒绝连接，需要同时更新两端。

服务端 Schema 响应单独限制为 900,000 个 UTF-8 字节，包含标识符、反馈和通道字段后仍低于客户端 1 MiB 消息上限，发送与接收两端均校验。`SchemaPacketTest` 验证 ASCII、中文和 emoji 的精确字节边界、真实包编码往返及超限拒绝。

## Schema 命令验证

```mcfunction
/htlib ide schema minecraft:damage_type
/htlib ide schema minecraft:worldgen/configured_feature json
/htlib ide schema types minecraft:worldgen/configured_feature
```

第二条命令应生成 `schema_output/minecraft_worldgen/configured_feature.json`；导出目录的嵌套部分由命令创建。注册表路径中的空段、`.` 和 `..` 不作为文件系统路径解释。文件或中间目录指向输出目录之外时拒绝写入。

JSON 导出仅反馈文件路径，不同时把整份结构发到聊天框；文本查询最多发送 128 行，并提示使用 JSON 导出完整内容，避免大型配置刷屏。

`SchemaRegistryAccessTest` 检查嵌套上下文恢复、异常清理和线程隔离；`SchemaFileExporterTest` 检查嵌套导出、UTF-8、覆盖后的截断、异常路径拒绝及父目录为文件的情况。原有 `CodecSchemaParserTest` 继续验证作者已支持的解析形态。

本次迁移已运行上述测试：原有解析测试 13 项与新增回归测试 12 项全部通过。

## 验证范围

使用 Java 17 和本仓库 Gradle Wrapper 运行 `./gradlew test build`（Windows 为 `.\gradlew.bat test build`）。独立编辑器此前的测试数量和游戏内实测不能算作本次 HTLib 构建的验证结果。

本次 HTLib 全量 `test build` 已通过：138 项测试中 135 项通过，3 项因 Windows 环境缺少符号链接创建权限而跳过，无失败。构建产物为 `build/libs/HTLib-1.20.1-1.3.0.jar`。这些自动测试不代替新版客户端的游戏内验证。

### 游戏内主流程实测（2026-11-05）

在本仓库 `runClient` 启动的 Minecraft 1.20.1 / Forge 47.2.0 中，使用独立世界 `HTLib Migration Test` 完成以下验证：

- `/htlib ide editor` 正常打开，选择 `minecraft:damage_type` 后加载服务端 Schema。
- `exhaustion` 输入 `abc` 时草稿保留并标红，点击校验会阻止提交；改为 `0.5` 后恢复正常。切换 JSON 视图时合法空字符串仍保留，可选字段未启用时不输出。
- 从“更多”导入 JSON，输入框正常获得焦点；缩小和恢复窗口后导入内容仍在，确认后服务端校验通过。
- 数据包名称 `migration_pack`、资源 ID `migration:sample` 分开填写，保存到 `datapacks/migration_pack/data/migration/damage_type/sample.json`，生成格式 15 的 `pack.mcmeta`。再将 `exhaustion` 改为 `0.75` 覆盖保存，JSON 更新，元数据 SHA-256 保持不变。
- 首次新建的数据包先运行 `/datapack list available` 刷新发现，再运行 `/datapack enable "file/migration_pack"` 启用。本轮重新进入世界后执行下列命令，游戏返回“已将 1.0 伤害施加于猪”，确认编辑器生成的资源已被实际加载；未手工修改生成的 JSON。

```mcfunction
/summon minecraft:pig ~ 100 ~ {Tags:["htlib_migration"],NoAI:1b,NoGravity:1b}
/damage @e[type=minecraft:pig,tag=htlib_migration,limit=1] 1 migration:sample
```

原生截图保存在本地 `run/screenshots/2026-11-05_15.37.26.png`（JSON 视图）和 `run/screenshots/2026-11-05_15.44.35.png`（实际伤害调用成功）。测试存档与截图均未加入版本控制。

本次迁移不宣称已完整支持所有 Codec。复杂递归、Either、未知结构和默认值的精确编码仍应分别以真实 Codec 的解析与往返用例验证；服务端校验继续作为保存前的最终依据。
