package dev.tagtag.common.model;

import dev.tagtag.common.constant.GlobalConstants;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PageQuery(
        @Min(1)
        @JsonAlias({"pageNumber", "currentPage", "current"})
        int pageNo,
        @Min(1)
        @Max(GlobalConstants.MAX_PAGE_SIZE)
        @JsonAlias({"pageSize", "limit", "size"})
        int pageSize
) {
    public PageQuery() {
        this(GlobalConstants.DEFAULT_PAGE_NO, GlobalConstants.DEFAULT_PAGE_SIZE);
    }

    public PageQuery normalized() {
        int pn = GlobalConstants.normalizePageNo(this.pageNo);
        int ps = GlobalConstants.clampPageSize(this.pageSize);
        return new PageQuery(pn, ps);
    }

    public int toOffset() {
        PageQuery n = this.normalized();
        return (n.pageNo - 1) * n.pageSize;
    }
}


