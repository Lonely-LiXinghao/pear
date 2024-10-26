package cn.muzisheng.pear.properties;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("app.log")
public class LogProperties {
    private String level;
    private String filePath;
    private String filePattern;
    private String stdoutPattern;
    @PostConstruct
    public void validate() {
        if(level!=null){
            try {
                LogLevel.valueOf(level);
            }catch (IllegalArgumentException e){
                throw new IllegalArgumentException("Invalid log level: " + level);
            }
        }
    }

    public enum LogLevel {
        TRACE("TRACE"),
        DEBUG("DEBUG"),
        INFO("INFO"),
        WARN("WARN"),
        ERROR("ERROR");

        LogLevel(String value) {
        }
    }
}
