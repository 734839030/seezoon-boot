package com.seezoon.admin.modules.sys.web;

import com.github.pagehelper.PageInfo;
import com.seezoon.boot.common.web.BaseController;
import com.seezoon.boot.context.dto.ResponeModel;
import com.seezoon.service.modules.sys.entity.SysParam;
import com.seezoon.service.modules.sys.service.SysParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;

@RestController
@RequestMapping("${admin.path}/sys/param")
public class SysParamController extends BaseController {

    @Autowired
    private SysParamService sysParamService;

    @PreAuthorize("hasAuthority('sys:param:qry')")
    @PostMapping("/qryPage")
    public ResponeModel qryPage(@RequestBody SysParam sysParam) {
        PageInfo<SysParam> page = sysParamService.findByPage(sysParam, sysParam.getPage(), sysParam.getPageSize());
        return ResponeModel.ok(page);
    }

    @PreAuthorize("hasAuthority('sys:param:qry')")
    @RequestMapping("/get")
    public ResponeModel get(@RequestParam Serializable id) {
        SysParam sysParam = sysParamService.findById(id);
        return ResponeModel.ok(sysParam);
    }

    @PreAuthorize("hasAuthority('sys:param:save')")
    @PostMapping("/save")
    public ResponeModel save(@Validated @RequestBody SysParam sysParam) {
        int cnt = sysParamService.save(sysParam);
        return ResponeModel.ok(cnt);
    }

    @PreAuthorize("hasAuthority('sys:param:update')")
    @PostMapping("/update")
    public ResponeModel update(@Validated @RequestBody SysParam sysParam) {
        int cnt = sysParamService.updateSelective(sysParam);
        return ResponeModel.ok(cnt);
    }

    @PreAuthorize("hasAuthority('sys:param:delete')")
    @PostMapping("/delete")
    public ResponeModel delete(@RequestParam Serializable id) {
        int cnt = sysParamService.deleteById(id);
        return ResponeModel.ok(cnt);
    }

    @PostMapping("/checkParamKey")
    public ResponeModel checkParamKey(@RequestParam(required = false) String id, @RequestParam String paramKey) {
        SysParam sysParam = sysParamService.findByParamKey(paramKey.trim());
        return ResponeModel.ok(sysParam == null || sysParam.getId().equals(id));
    }

}
