package com.seezoon.boot.configuration;

import com.seezoon.boot.common.sms.SmsProperties;
import com.seezoon.boot.common.sms.SmsSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 阿里云短信
 *
 * @author hdf
 */
@Configuration
@EnableConfigurationProperties(SmsProperties.class)
public class SmsSenderAutoConfiguration {

    @Autowired
    private SmsProperties smsProperties;

    @Bean
    @ConditionalOnProperty(name = "sms.accessKeyId")
    public SmsSender smsSender() {
        SmsSender smsSender = new SmsSender(smsProperties.getAccessKeyId(), smsProperties.getAccessKeySecret());
        return smsSender;
    }
}
