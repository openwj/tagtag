package dev.tagtag.module.iam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import dev.tagtag.common.model.SortField;
import dev.tagtag.contract.iam.dto.MenuQueryDTO;
import dev.tagtag.module.iam.entity.Menu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MenuMapper extends BaseMapper<Menu> {

    /**
     * 权限分页查询（由 MyBatis XML 构建 WHERE/ORDER BY）
     * @param page 分页对象
     * @param q 查询条件
     * @param orderList 排序字段列表
     * @return 分页结果
     */
    IPage<Menu> selectPage(IPage<Menu> page, @Param("q") MenuQueryDTO q, @Param("orderList") List<SortField> orderList);
}
