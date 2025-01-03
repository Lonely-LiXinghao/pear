package cn.muzisheng.pear.properties;

import cn.muzisheng.pear.constant.Constant;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(UserProperties.class)
@EnableConfigurationProperties(UserProperties.class)
public class UserAutoConfigure {
    @Bean("userProperties")
    @ConditionalOnMissingBean
    public UserProperties tokenProperties() {
        return new UserProperties(Constant.APP_USER_PASSWORD_SALT);
    }
}
