# 系统统计接口契约

## 总览
- `GET /api/sys/stats/overview`
  - 返回：
    - `usersTotal`、`rolesTotal`、`deptsTotal`
    - `messagesTotal`、`unreadMessages`
    - `filesTotal`
    - `dictTypesTotal`、`dictDataTotal`

## 趋势
- `GET /api/sys/stats/trends?days=30`
  - 参数：`days`（可选，默认 30）
  - 返回：
    - `labels: string[]`（yyyy-MM-dd）
    - `userCreatedPerDay: number[]`
    - `messageCreatedPerDay: number[]`
    - `fileUploadedPerDay: number[]`

## 分布
- `GET /api/sys/stats/files/distribution?by=type|storage|ext`
  - 返回：`Array<{ name: string; value: number }>`
- `GET /api/sys/stats/messages/distribution?by=status|type`
  - 返回：`Array<{ name: string; value: number }>`

## 权限
- 访问需具备权限码：`stats:read`（完整权限为 `PERM_stats:read`）

## 说明
- 所有计数均过滤逻辑删除（`deleted = 0`）。
- 趋势查询按 `create_time` 聚合，缺失日期补 0。
- 文件分布维度对应：`type→mime_type`、`storage→storage_type`、`ext→扩展名`。
