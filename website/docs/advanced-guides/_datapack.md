数据包文件结构目录如下：

```plain
.
├── assets
│   └── mod_id
│       └── extra_models
│           └── xxx.json
└── data
    └── mod_id
        └── htlib
            ├── raid_item
            ├── raid
            ├── wave
            ├── spawn
            ├── position
            └── result
```

---

# 袭击物品
| 字段                | 类型     | 描述                           |
|-------------------|--------|------------------------------|
| item_setting      | 物品设置   | 物品相关设置。                      |
| dummy_entity_type | 虚拟实体类型 | 暂时用处不大，填 htlib:default_raid。 |
| raid              | 注册名    | 物品触发的袭击的注册名。                 |


## 物品设置
| 字段     | 类型   | 描述           |
|--------|------|--------------|
| name   | 字符串  | 袭击物品的别名。     |
| model  | 字符串  | 袭击物品的模型。     |
| colors | list | 袭击物品各个贴图的颜色。 |
| texts  | list | 袭击物品的相应描述。   |
# 袭击组件
| 类型           | 描述   |
|--------------|------|
| htlib:common | 一般袭击 |


## 袭击设置
| 字段               | 类型        | 描述                  |
|------------------|-----------|---------------------|
| placement_type   | 放置组件      | 袭击相关的设置，默认为default。 |
| border_setting   | 边界设置      | 袭击边界相关的设置。          |
| bar_setting      | 袭击条设置     | 袭击进度条相关的设置。         |
| sound_setting    | 音效设置      | 袭击相关音效的设置。          |
| victory_results  | 结果组件 list | 袭击胜利结果相关组件，默认无。     |
| loss_results     | 结果组件 list | 袭击失败结果相关组件，默认无。     |
| victory_duration | int       | 袭击胜利持续时间，默认为100。    |
| loss_duration    | int       | 袭击失败持续时间，默认为100。    |
| show_wave_title  | bool      | 是否展示每波标题，默认true。    |


### 边界设置
| 字段            | 类型     | 描述                 |
|---------------|--------|--------------------|
| raid_range    | double | 袭击边界的半径，默认为40。     |
| block_inside  | bool   | 是否阻挡进入边界，默认false。  |
| block_outside | bool   | 是否阻挡离开边界，默认为false。 |
| render_border | bool   | 是否渲染边界，默认为false。   |
| border_color  | int    | 边界的颜色，默认为原版边界颜色。   |


### 音效设置
| 字段               | 类型     | 描述              |
|------------------|--------|-----------------|
| raid_start_sound | string | 袭击开始时播放的音效，可空。  |
| wave_start_sound | string | 一大波开始时播放的音效，可空。 |
| victory_sound    | string | 袭击胜利时播放的音效，可空。  |
| loss_sound       | string | 袭击失败时播放的音效，可空。  |


### 袭击栏设置
| 字段             | 类型     | 描述              |
|----------------|--------|-----------------|
| raid_title     | string | 袭击标题，有默认。       |
| raid_bar_color | string | 字段见下截图，默认red。   |
| victory_title  | string | 袭击胜利时显示的标题，有默认。 |
| loss_title     | string | 袭击失败时显示的标题，有默认。 |

## 一般袭击
| 字段      | 类型       | 描述       |
|---------|----------|----------|
| setting | 袭击设置     | 袭击相关的设置。 |
| waves   | 波组件 list | 袭击的波。    |


# 波组件
波组件需要在**<font style="color:#DF2A3F;">custom_raids/waves</font>**文件夹下增加。

| 类型           | 描述  |
|--------------|-----|
| htlib:common | 一般波 |


## 波设置
| 字段               | 类型     | 描述                       |
|------------------|--------|--------------------------|
| spawn_placement  | 放置组件   | 怪物生成方式，优先级中，默认无。         |
| prepare_duration | int    | 此波的预先准备时间，默认为100tick。    |
| wave_duration    | int    | 此波持续的时间，默认为0，0表示无限时间。    |
| can_skip_wave    | bool   | 袭击者全部死亡后会自动开始下一波，默认true。 |
| wave_start_sound | string | 此波开始时播放的音效，优先级高，可空。      |


## 一般波
| 字段 | 类型 | 描述 |
| --- | --- | --- |
| setting | 波设置 | 波相关的设置。 |
| spawns | 生成组件 list | 每波生成的选项。 |


# 生成组件
生成组件需要在**<font style="color:#DF2A3F;">custom_raids/spawns</font>**文件夹下增加。

| 类型 | 描述 |
| --- | --- |
| htlib:once | 立刻生成 |
| htlib:duration | 持续生成 |


## 生成设置
| 字段 | 类型 | 描述 |
| --- | --- | --- |
| entity_type | string | 袭击者的实体类型。 |
| nbt | nbt | 此袭击者追加的NBT，默认无。 |
| enable_default_spawn | bool | 开启原版内置生成附加，默认true。 |
| spawn_placement | 放置组件 | 决定袭击者的放置方式，优先级高，可空。 |


## 立刻生成
| 字段 | 类型 | 描述 |
| --- | --- | --- |
| setting | 生成设置 | 生成相关的设置。 |
| spawn_tick | int | 生成时刻。 |
| spawn_count | int | 生成数量。 |


## 持续生成
| 字段 | 类型 | 描述 |
| --- | --- | --- |
| setting | 生成设置 | 生成相关的设置。 |
| start_tick | int | 生成时刻，默认为0。 |
| duration | int | 生成持续时间。 |
| spawn_interval | int | 生成的间隔。 |
| each_spawn_count | int | 每次生成的数量。 |
| spawn_offset | int | 忘了？默认为0。 |


# 放置组件
放置组件需要在**<font style="color:#DF2A3F;">custom_raids/placements</font>**文件夹下增加。

| 类型 | 描述 |
| --- | --- |
| htlib:center_area | 中心区域放置 |
| htlib:absolute_area | 绝对区域放置 |


## 中心区域放置
| 字段 | 类型 | 描述 |
| --- | --- | --- |
| center_offset | vec3 | 距离袭击点中心的偏移，默认无。 |
| exclude_radius | double | 最短生成半径，默认为0。 |
| radius | double | 最长生成半径。 |
| on_surface | bool | 是否生成在地表。 |
| height_offset | double | 在地表不启用时的相对偏移，默认为0。 |
| is_circle | bool | 生成范围是圆还是正方形，默认是true。 |


## 绝对区域放置
| 字段 | 类型 | 描述 |
| --- | --- | --- |
| position | vec3 | 绝对坐标。 |
| exclude_radius | double | 最短生成半径，默认为0。 |
| radius | double | 最长生成半径。 |
| is_circle | bool | 生成范围是圆还是正方形，默认是true。 |


# 结果组件
放置组件需要在**<font style="color:#DF2A3F;">custom_raids/results</font>**文件夹下增加。

| 类型 | 描述 |
| --- | --- |
| htlib:item_stack | 物品结果 |
| htlib:chest | 箱子结果 |
| htlib:event | 事件结果 |
| htlib:function | 函数结果 |
| htlib:command | 命令结果 |
| htlib:clear_raider | 移除袭击者结果 |

## 物品结果
| 字段 | 类型 | 描述 |
| --- | --- | --- |
| for_raider | bool | 是否给袭击者，默认为false。 |
| for_defender | bool | 是否给防卫者，默认为true。 |
| rewards | 物品 list | 给予的物品。 |


## 箱子结果
| 字段 | 类型 | 描述 |
| --- | --- | --- |
| on_ground | bool | 是否放置在袭击中心的地面，默认为true。 |
| loot | string | 箱子里面放置物品的战利品表。 |


## 物品结果（联动KubeJS）
| 字段 | 类型 | 描述 |
| --- | --- | --- |
| for_level | bool | 是否触发世界，默认为false。 |
| for_raider | bool | 是否触发袭击者，默认为false。 |
| for_defender | bool | 是否触发防卫者，默认为false。 |
| id | string | 事件标识。 |


## 函数结果
函数指向原版functions文件夹下的.mcfunction文件。

| 字段 | 类型 | 描述 |
| --- | --- | --- |
| global_functions | string 列表 | 是否触发全局函数（无目标实体），默认为空。 |
| defender_functions | string 列表 | 是否触发针对防卫者的函数，默认为空。 |
| raider_functions | string 列表 | 是否触发针对袭击者的函数，默认为空。 |


## 指令结果
| 字段 | 类型 | 描述 |
| --- | --- | --- |
| global_command | string | 是否触发全局命令（无目标实体），默认为空。 |
| defender_command | string | 是否触发针对防卫者的命令，默认为空。 |
| raider_command | string | 是否触发针对袭击者的命令，默认为空。 |

## 移除袭击者结果
| 字段 | 类型 | 描述 |
| --- | --- | --- |
| clear_tick | int | 在哪个tick移除，默认为0。 |