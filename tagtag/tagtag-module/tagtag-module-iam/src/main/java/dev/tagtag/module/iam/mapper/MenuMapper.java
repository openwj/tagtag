package dev.tagtag.module.iam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import dev.tagtag.contract.iam.dto.MenuQueryDTO;
import dev.tagtag.module.iam.entity.Menu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


@Mapper
public interface MenuMapper extends BaseMapper<Menu> {

    /**
     * 权限分页查询（由 MyBatis XML 构建 WHERE/ORDER BY）
     * @param page 分页对象
     * @param q 查询条件
     * 固定排序由 XML 定义（create_time DESC, id DESC），不接受前端传入
     * @return 分页结果
     */
    IPage<Menu> selectPage(IPage<Menu> page, @Param("q") MenuQueryDTO q);
}
