package com.seezoon.service.modules.sys.dao;

import com.seezoon.boot.common.dao.CrudDao;
import com.seezoon.service.modules.sys.entity.SysMenu;

import java.io.Serializable;
import java.util.List;

public interface SysMenuDao extends CrudDao<SysMenu> {

    public List<SysMenu> findByRoleId(String roleId);

    public List<SysMenu> findByUserId(String userId);

    public int deleteRoleMenuByMenuId(Serializable menuId);

}
