package com.sm.study_manager;

import java.time.LocalDateTime;

public class TimerLogEntry {
    // 생성자
    TimerLogEntry(LocalDateTime startTime , LocalDateTime endTime){
        this.startTime = startTime;
        this.endTime = endTime;
    }
    
    
    private LocalDateTime startTime;

    private LocalDateTime endTime;

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }


}
