package com.techday.reservation.pingpong.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Table {
    private TableStatus status;
    private long timeRemaining;
}
