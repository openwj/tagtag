package dev.tagtag.common.model;
import dev.tagtag.common.util.PageUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {
    private List<T> list;
    private long total;
    private int pageNo;
    private int pageSize;

    /**
     * 创建分页结果
     * @param list 当前页数据
     * @param total 总记录数
     * @param pageNo 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    public static <T> PageResult<T> of(List<T> list, long total, int pageNo, int pageSize) {
        PageResult<T> pr = new PageResult<>();
        pr.setList(list == null ? Collections.emptyList() : list);
        pr.setTotal(Math.max(total, 0L));
        pr.setPageNo(PageUtil.normalizePageNo(pageNo));
        pr.setPageSize(PageUtil.clampPageSize(pageSize));
        return pr;
    }

    /**
     * 创建空分页结果
     * @param pageNo 页码
     * @param pageSize 每页大小
     * @return 空结果
     */
    public static <T> PageResult<T> empty(int pageNo, int pageSize) {
        return of(Collections.emptyList(), 0L, pageNo, pageSize);
    }

    /**
     * 转换结果数据类型（保持分页信息）
     * @param mapper 转换函数
     * @return 转换后的分页结果
     */
    public <R> PageResult<R> map(Function<T, R> mapper) {
        List<R> mapped = this.list == null ? Collections.emptyList() : this.list.stream().map(mapper).toList();
        return of(mapped, this.total, this.pageNo, this.pageSize);
    }

    /**
     * 计算总页数（向上取整）
     * @return 总页数
     */
    public long getTotalPages() {
        if (this.pageSize <= 0) {
            return 0;
        }
        return (this.total + this.pageSize - 1L) / this.pageSize;
    }
}
