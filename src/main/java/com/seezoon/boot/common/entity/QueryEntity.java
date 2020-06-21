package com.seezoon.boot.common.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Splitter;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.seezoon.boot.common.Constants;
import com.seezoon.boot.common.annotation.SortField;
import com.seezoon.boot.context.dto.AdminUser;
import com.seezoon.boot.context.utils.AdminThreadContext;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryEntity implements Serializable {

    private static final Map<String, String> SORT_DIRECTION_MAPPING = new HashMap<String, String>() {
        {
            put("ascending", "asc");
            put("asc", "asc");
            put("descending", "desc");
            put("desc", "desc");
        }
    };

    // <classname,property,dbField>
    private static final Table<String, String, String> SORT_FIELD_MAPPING = HashBasedTable.create();

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    /**
     * 排序字段，对应db字段名
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String sortField;
    /**
     * 升降序
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String direction;
    /**
     * 页码
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer page = 1;
    /**
     * 每页大小
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer pageSize = 20;
    /**
     * 自定义查询字段
     */
    private Map<String, Object> ext;
    /**
     * 当前表的别名，默认sql语句没有别名，当有别名时候，为了避免冲突需要子类指定
     */
    @JsonIgnore
    private String tableAlias;
    /**
     * dataScopeFilter
     */
    @JsonIgnore
    private String dsf;
    /**
     * 是否启用数据权限 默认开启
     */
    @JsonIgnore
    private boolean openDsf = true;

    public QueryEntity() {
        Class<? extends QueryEntity> childClass = this.getClass();
        String className = childClass.getName();
        if (!SORT_FIELD_MAPPING.containsRow(className)) {
            SortField sortFieldAnnotation = childClass.getDeclaredAnnotation(SortField.class);
            if (null != sortFieldAnnotation) {
                String[] value = sortFieldAnnotation.value();
                if (ArrayUtils.isEmpty(value)) {
                    return;
                }
                for (String v : value) {
                    List<String> list = Splitter.on(Constants.COLON).trimResults().omitEmptyStrings().splitToList(v);
                    Assert.isTrue(list.size() == 2, "排序字段规则错误xxx:yyyy");
                    SORT_FIELD_MAPPING.put(className, list.get(0), list.get(1));
                }
            }
        }
    }


    public String getDsf() {
        // /a 路径的后端请求需要后端需要，前端不需要
        AdminUser user = AdminThreadContext.getUser();
        if (user != null && StringUtils.isNotEmpty(user.getDsf()) && StringUtils.isEmpty(dsf) && this.openDsf()) {
            dsf = user.getDsf();
            //填充别名
            if (StringUtils.isNotEmpty(this.getTableAlias())) {
                dsf = dsf.replace("{TABLE_ALIAS}", this.getTableAlias() + ".");
            } else {
                dsf = dsf.replace("{TABLE_ALIAS}", "");
            }
        }
        return dsf;
    }


    public void setDsf(String dsf) {
        this.dsf = dsf;
    }

    public boolean openDsf() {
        return openDsf;
    }


    public void setOpenDsf(boolean openDsf) {
        this.openDsf = openDsf;
    }


    public String getTableAlias() {
        return tableAlias;
    }

    /**
     * 添加自定义参数
     *
     * @param key
     * @param value
     * @return
     */
    public Map<String, Object> addProperty(String key, Object value) {
        if (null == value) {
            return ext;
        }
        if (ext == null) {
            ext = new HashMap<>(1);
        }
        ext.put(key, value);
        return ext;
    }

    public String getSortField() {
        if (StringUtils.isNotEmpty(sortField)) {
            return SORT_FIELD_MAPPING.get(this.getClass().getName(), sortField);
        }
        return null;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public String getDirection() {
        return SORT_DIRECTION_MAPPING.get(direction);
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public Map<String, Object> getExt() {
        return ext;
    }

    public void setExt(Map<String, Object> ext) {
        this.ext = ext;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        if (null != pageSize && pageSize > 1000) {
            pageSize = 1000;
        }
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

}
