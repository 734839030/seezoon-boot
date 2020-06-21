package com.seezoon.service.modules.sys.service;


import com.seezoon.boot.BaseApplicationTest;
import com.seezoon.service.modules.sys.entity.SysParam;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class SysParamServiceTest extends BaseApplicationTest {

    @Autowired
    private SysParamService sysParamService;

    @Test
    public void testSave() {
        for (int i = 0; i < 1001; i++) {
            SysParam sysParam = new SysParam();
            sysParam.setName("名称" + i);
            sysParam.setParamKey("key" + i);
            sysParam.setParamValue("key" + i);
            sysParamService.save(sysParam);
        }
    }

}
