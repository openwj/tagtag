package dev.tagtag.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeRangeDTO {

    private LocalDateTime start;
    private LocalDateTime end;

    /**
     * 工厂方法：创建时间范围
     *
     * @param start 起始时间
     * @param end 结束时间
     * @return TimeRangeDTO
     */
    public static TimeRangeDTO of(LocalDateTime start, LocalDateTime end) {
        return TimeRangeDTO.builder().start(start).end(end).build();
    }
}

