package dev.tagtag.framework.util;

import dev.tagtag.framework.config.PageProperties;
import dev.tagtag.common.model.PageQuery;
import dev.tagtag.common.constant.GlobalConstants;

/**
 * 分页归一化适配器：应用配置的默认值与上限，安全归一化 PageQuery
 */
public final class PageNormalizer {

    private final PageProperties props;

    /**
     * 构造函数：注入 PageProperties
     * @param props 分页配置属性
     */
    public PageNormalizer(PageProperties props) {
        this.props = props;
    }

    /**
     * 归一化 pageNo/pageSize（应用默认值与上限），返回副本
     * @param pq 输入的分页参数
     * @return 归一化后的分页参数
     */
    public PageQuery normalize(PageQuery pq) {
        PageQuery out = (pq == null ? new PageQuery() : pq);
        Integer pageNo = out.getPageNo();
        Integer pageSize = out.getPageSize();

        // pageNo 使用全局默认与规范化规则
        int pn = (pageNo == null || pageNo < 1) ? GlobalConstants.DEFAULT_PAGE_NO : pageNo;

        // pageSize 使用配置默认值与上限
        int ps;
        if (pageSize == null || pageSize < 1) {
            ps = props.getDefaultPageSize();
        } else {
            ps = Math.min(pageSize, props.getMaxPageSize());
        }

        out.setPageNo(pn);
        out.setPageSize(ps);
        return out;
    }
}