package dev.tagtag.framework.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.tagtag.common.constant.GlobalConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Jackson JSON 序列化配置
 */
@Configuration
public class JacksonConfig {

    /**
     * 配置全局 ObjectMapper（时间格式、空值忽略、JavaTime 支持）
     * @return ObjectMapper
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.registerModule(new JavaTimeModule());
        mapper.setDefaultPrettyPrinter(null);
        // 使用项目默认格式
        mapper.setDateFormat(new java.text.SimpleDateFormat(GlobalConstants.FORMAT_DATETIME));
        return mapper;
    }
}
