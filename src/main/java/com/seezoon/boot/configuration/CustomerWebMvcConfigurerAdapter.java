package com.seezoon.boot.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.seezoon.admin.interceptor.AdminInterceptor;
import com.seezoon.front.session.FrontSessionInterceptor;

/**
 * 可以配置拦截器，返回值处理，请求改写，静态资源等spring mvc 属性 
 * 
 * @author hdf
 *
 */
@Configuration
public class CustomerWebMvcConfigurerAdapter implements WebMvcConfigurer{
	@Autowired
	private Environment environment;
	/**
	 * 拦截器配置
	 * 
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		//针对后端生效
		registry.addInterceptor(new AdminInterceptor()).addPathPatterns(environment.getProperty("admin.path") + "/**");
		registry.addInterceptor(new FrontSessionInterceptor()).addPathPatterns(environment.getProperty("front.path") + "/**");
		WebMvcConfigurer.super.addInterceptors(registry);
	}
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		//静态资源放在项目中时候起作用
		registry.addViewController("/").setViewName("forward:/admin/pages/index.html");
		registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
		WebMvcConfigurer.super.addViewControllers(registry);
	}
}
