package dev.tagtag.framework.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import dev.tagtag.common.constant.GlobalConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

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

        // 自定义 Java8 时间类型的序列化/反序列化，统一去除 ISO "T" 分隔符
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        DateTimeFormatter dateFormatter = GlobalConstants.DATE_FORMATTER;
        DateTimeFormatter dateTimeFormatter = GlobalConstants.DATETIME_FORMATTER;

        // LocalDateTime: yyyy-MM-dd HH:mm:ss
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(dateTimeFormatter));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(dateTimeFormatter));
        // LocalDate: yyyy-MM-dd
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(dateFormatter));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(dateFormatter));
        // LocalTime: HH:mm:ss
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(timeFormatter));
        javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(timeFormatter));

        mapper.registerModule(javaTimeModule);

        // Long/long 序列化为字符串，避免前端精度丢失
        SimpleModule numberToStringModule = new SimpleModule();
        numberToStringModule.addSerializer(Long.class, ToStringSerializer.instance);
        numberToStringModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
        mapper.registerModule(numberToStringModule);

        // 统一时区为项目默认（Asia/Shanghai）
        mapper.setTimeZone(TimeZone.getTimeZone(GlobalConstants.DEFAULT_ZONE_ID));

        // 统一 Date/Calendar 的输出格式（非 JavaTime 类型）
        mapper.setDefaultPrettyPrinter(null);
        mapper.setDateFormat(new java.text.SimpleDateFormat(GlobalConstants.FORMAT_DATETIME));
        return mapper;
    }
}
