package com.procurement.enterprise.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class RequestTimelineEntry {
    private String stage;
    private String status;
    private String actorName;
    private String remarks;
    private LocalDateTime timestamp;
}
