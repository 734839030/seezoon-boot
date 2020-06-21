package com.seezoon.boot.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 排序字段后台别名
 * eg.
 * <code>@SortField({"updateDate:t.update_date"})</code>
 *
 * @author hdf
 * @date 2020/6/19 8:11 下午
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface SortField {
    String[] value();
}
