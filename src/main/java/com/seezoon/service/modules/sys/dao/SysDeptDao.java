package com.seezoon.service.modules.sys.dao;

import com.seezoon.boot.common.dao.CrudDao;
import com.seezoon.service.modules.sys.entity.SysDept;

import java.io.Serializable;

public interface SysDeptDao extends CrudDao<SysDept> {
    public int deleteRoleDeptByDeptId(Serializable deptId);
}
