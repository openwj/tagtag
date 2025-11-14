package dev.tagtag.module.iam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import dev.tagtag.common.model.SortField;
import dev.tagtag.contract.iam.dto.DeptQueryDTO;
import dev.tagtag.module.iam.entity.Dept;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface DeptMapper extends BaseMapper<Dept> {

    /** 分页查询部门（由 XML 构建 WHERE/ORDER BY） */
    IPage<Dept> selectPage(IPage<Dept> page, @Param("q") DeptQueryDTO q, @Param("orderList") List<SortField> orderList);

    /** 查询部门的父ID */
    Long selectParentId(@Param("id") Long id);
}
