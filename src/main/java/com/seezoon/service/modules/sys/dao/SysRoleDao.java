package com.seezoon.service.modules.sys.dao;

import com.seezoon.boot.common.dao.CrudDao;
import com.seezoon.service.modules.sys.entity.SysRole;
import com.seezoon.service.modules.sys.entity.SysRoleDept;
import com.seezoon.service.modules.sys.entity.SysRoleMenu;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.List;

public interface SysRoleDao extends CrudDao<SysRole> {
    public int deleteRoleMenuByRoleId(Serializable roleId);

    public int deleteUserRoleByRoleId(Serializable roleId);

    public int deleteUserRoleByRoleIdAndUserId(@Param("roleId") String roleId, @Param("userId") String userId);

    public int insertUserRole(@Param("roleId") String roleId, @Param("userId") String userId);

    public int insertRoleMenu(List<SysRoleMenu> roleMenus);

    public List<SysRole> findByUserId(String userId);

    public int deleteRoleDeptByRoleId(Serializable roleId);

    public int insertRoleDept(List<SysRoleDept> list);

    public List<String> selectDeptIdsByRoleId(String roleId);
}
