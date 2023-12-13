package com.sm.study_manager;

import java.time.LocalDateTime;

// 현재시간 시작시간 종료시간 클래스 따로 뺐어요.
public class TimerLogEntry {
    // 생성자
    public TimerLogEntry(LocalDateTime startTime, LocalDateTime endTime, int durationInSeconds) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.durationInSeconds = durationInSeconds;
    }
    
    
    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private int durationInSeconds;  // 흐른시간

    public int getDurationInSeconds() {
        return durationInSeconds;
    }

    public void setDurationInSeconds(int durationInSeconds) {
        this.durationInSeconds = durationInSeconds;
    }

    // get set 추가 .
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


    @Override
    public String toString() {
        return "시작 시간: " + startTime + ", 종료 시간: " + endTime + ", 공부 시간: " + durationInSeconds + "초";
    }


}
