# 工具类边界说明

- `dev.tagtag.common.util.StringUtils`：提供通用字符串判空、裁剪、拼接等基础能力，适用于通用层。
- `dev.tagtag.common.util.Strings`：提供更语义化的规范化接口（如 `normalizeToNull`），用于契约/模型层的简化调用。

约定：
- 若已有等价能力，优先使用 `StringUtils`，`Strings` 作为语义包装不重复实现复杂逻辑。
- 新增字符串能力统一收敛到 `StringUtils`，`Strings` 仅暴露语义化别名，避免重复维护。
