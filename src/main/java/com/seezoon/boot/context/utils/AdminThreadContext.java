package com.seezoon.boot.context.utils;

import com.seezoon.boot.context.dto.AdminUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 后端管理线程上下文
 *
 * @author hdf
 */
public class AdminThreadContext {

    protected static Logger logger = LoggerFactory.getLogger(AdminThreadContext.class);
    /**
     * 利用了容器的线程池，ThreadLocal 数据会相互干扰
     */
    private static ThreadLocal<AdminUser> threadLocal = new ThreadLocal<>();

    public static void putUser(AdminUser adminUser) {
        threadLocal.remove();
        threadLocal.set(adminUser);
    }

    public static AdminUser getUser() {
        AdminUser adminUser = threadLocal.get();
        return adminUser;
    }

    public static void removeUser() {
        threadLocal.remove();
    }
}
