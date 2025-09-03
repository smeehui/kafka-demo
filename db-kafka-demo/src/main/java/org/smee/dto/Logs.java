package org.smee.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class Logs {
    private Integer id;
    private String log;
    private Date time;
    @JsonProperty("log_type")
    private LogType logType;
    private String before;
    private String after;
}
