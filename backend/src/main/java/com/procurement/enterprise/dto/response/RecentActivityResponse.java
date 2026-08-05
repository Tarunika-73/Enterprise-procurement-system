package com.procurement.enterprise.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * A single entry in the Reports page "Recent Activity" feed.
 */
@Getter
@Builder
public class RecentActivityResponse {

    private String description;
    private LocalDateTime timestamp;
}
