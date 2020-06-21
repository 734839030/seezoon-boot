package com.seezoon.service.modules.sys.entity;

import com.seezoon.boot.common.annotation.SortField;
import com.seezoon.boot.common.entity.BaseEntity;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * 系统参数
 *
 * @author hdf 2018年4月1日
 */
@SortField({"name:name", "paramKey:param_key", "updateDate:update_date"})
public class SysParam extends BaseEntity<String> {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * 名称
     */
    @NotNull
    @Length(min = 1, max = 50)
    private String name;

    /**
     * 键
     */
    @NotNull
    @Length(min = 1, max = 50)
    private String paramKey;

    /**
     * 值
     */
    @NotNull
    @Length(min = 1, max = 50)
    private String paramValue;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParamKey() {
        return paramKey;
    }

    public void setParamKey(String paramKey) {
        this.paramKey = paramKey;
    }

    public String getParamValue() {
        return paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }
}
