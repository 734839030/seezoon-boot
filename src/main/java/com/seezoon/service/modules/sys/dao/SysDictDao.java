package com.seezoon.service.modules.sys.dao;

import com.seezoon.boot.common.dao.CrudDao;
import com.seezoon.service.modules.sys.entity.SysDict;

import java.util.List;

public interface SysDictDao extends CrudDao<SysDict> {
    public List<String> findTypes();
}
