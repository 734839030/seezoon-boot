package com.seezoon.service.modules.sys.dao;

import com.seezoon.boot.common.dao.CrudDao;
import com.seezoon.service.modules.sys.dto.DbTable;
import com.seezoon.service.modules.sys.dto.DbTableColumn;
import com.seezoon.service.modules.sys.entity.SysGen;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysGenDao extends CrudDao<SysGen> {

    public List<DbTable> findTable(@Param("tableName") String tableName);

    public List<DbTableColumn> findColumnByTableName(@Param("tableName") String tableName);

    public String findPkType(@Param("tableName") String tableName);
}
