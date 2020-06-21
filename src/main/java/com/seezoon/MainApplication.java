package com.seezoon;

import com.seezoon.boot.common.dao.BaseDao;
import com.seezoon.boot.context.utils.MdcUtil;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@MapperScan(basePackages = {"com.seezoon.service.modules.*.dao"}, markerInterface = BaseDao.class)
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableTransactionManagement(proxyTargetClass = true)
@EnableRedisHttpSession(redisNamespace = "${spring.session.redis.namespace}", maxInactiveIntervalInSeconds = 7200)
public class MainApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        MdcUtil.push();
        SpringApplication.run(MainApplication.class, args);
    }

}
