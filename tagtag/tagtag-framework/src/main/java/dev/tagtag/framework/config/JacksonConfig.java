package dev.tagtag.framework.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
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
import dev.tagtag.framework.security.util.SecurityHandlerUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.TimeZone;

@Configuration
public class JacksonConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        JavaTimeModule javaTimeModule = new JavaTimeModule();

        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(GlobalConstants.DATETIME_FORMATTER));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(GlobalConstants.DATETIME_FORMATTER));
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(GlobalConstants.DATE_FORMATTER));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(GlobalConstants.DATE_FORMATTER));
        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(GlobalConstants.TIME_FORMATTER));
        javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(GlobalConstants.TIME_FORMATTER));

        mapper.registerModule(javaTimeModule);

        SimpleModule numberToStringModule = new SimpleModule();
        numberToStringModule.addSerializer(Long.class, ToStringSerializer.instance);
        numberToStringModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
        mapper.registerModule(numberToStringModule);

        mapper.setTimeZone(TimeZone.getTimeZone(GlobalConstants.DEFAULT_ZONE_ID));
        mapper.setDefaultPrettyPrinter(null);
        mapper.setDateFormat(new java.text.SimpleDateFormat(GlobalConstants.FORMAT_DATETIME));

        SecurityHandlerUtils.setObjectMapper(mapper);

        return mapper;
    }
}
