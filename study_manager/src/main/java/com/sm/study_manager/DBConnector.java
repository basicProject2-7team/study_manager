package com.sm.study_manager;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DBConnector {
    private Connection connection;
    private PreparedStatement pstmt;
    private HikariDataSource dataSource;

    public DBConnector() {
        try {
//            (DataSource) DriverManager.getConnection(URL, USER, PASSWORD);
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:mariadb://10.20.33.68:3306/studydb"); // 데이터베이스 URL
            config.setUsername("user"); // 데이터베이스 사용자 이름
            config.setPassword("1234");
            dataSource = new HikariDataSource(config);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertLogEntryAndUpdateTotalTime(TimerLogEntry entry) {      // 로그에도 insert , total 데이터에도 insert
        String insertLogSql = "INSERT INTO timer_list_tb (STUDY_DATE, STUDY_START_TIME, STUDY_END_TIME, STUDY_JUST_TIME) VALUES (?, ?, ?, ?)";
        // timer_list_tb 에 저장
        String selectTotalSql = "SELECT TOTAL_STUDYTIME FROM study_tb WHERE TODAY = ?";
        // 오늘 총 공부시간 갖고옴
        String updateTotalSql = "UPDATE study_tb SET TOTAL_STUDYTIME = ? WHERE TODAY = ?";
        // 오늘 총 공부시간 업데이트
        String insertTotalSql = "INSERT INTO study_tb (TODAY, TOTAL_STUDYTIME) VALUES (?, ?)";
        // 오늘 총 공부시간에  추가
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);

            // 로그 삽입
            try (PreparedStatement logStmt = connection.prepareStatement(insertLogSql)) {
                logStmt.setDate(1, Date.valueOf(entry.getStartTime().toLocalDate()));
                logStmt.setTimestamp(2, Timestamp.valueOf(entry.getStartTime()));
                logStmt.setTimestamp(3, Timestamp.valueOf(entry.getEndTime()));
                logStmt.setInt(4, entry.getDurationInSeconds());
                logStmt.executeUpdate();
            }

            // 총 공부 시간 업데이트
            try (PreparedStatement selectStmt = connection.prepareStatement(selectTotalSql)) {
                selectStmt.setDate(1, Date.valueOf(entry.getStartTime().toLocalDate()));
                ResultSet rs = selectStmt.executeQuery();
                int newTotalStudyTime = entry.getDurationInSeconds();

                if (rs.next()) {
                    newTotalStudyTime += rs.getInt("TOTAL_STUDYTIME");
                    try (PreparedStatement updateStmt = connection.prepareStatement(updateTotalSql)) {
                        updateStmt.setInt(1, newTotalStudyTime);
                        updateStmt.setDate(2, Date.valueOf(entry.getStartTime().toLocalDate()));
                        updateStmt.executeUpdate();
                    }
                } else {
                    try (PreparedStatement insertStmt = connection.prepareStatement(insertTotalSql)) {
                        insertStmt.setDate(1, Date.valueOf(entry.getStartTime().toLocalDate()));
                        insertStmt.setInt(2, newTotalStudyTime);
                        insertStmt.executeUpdate();
                    }
                }
            }

            connection.commit(); // 트랜잭션 커밋
        } catch (SQLException e) {
            e.printStackTrace();
            if (connection != null) {
                try {
                    connection.rollback(); // 오류 발생 시 롤백
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        } finally {
            if (connection != null) {
                try {
                    connection.close(); // 연결 종료
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public List<TimerLogEntry> selectLogEntriesByDate(LocalDate date) {
        List<TimerLogEntry> entries = new ArrayList<>();
        String sql = "SELECT * FROM timer_list_tb WHERE STUDY_DATE = ?";

        try {
            connection = dataSource.getConnection();
            pstmt = connection.prepareStatement(sql);
            pstmt.setDate(1, Date.valueOf(date)); // SQL Date로 변환   // 현재날짜 매개변수로 sql 문에 보냄
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                LocalDateTime startTime = rs.getTimestamp("STUDY_START_TIME").toLocalDateTime();
                LocalDateTime endTime = rs.getTimestamp("STUDY_END_TIME").toLocalDateTime();
                int durationInSeconds = rs.getInt("STUDY_JUST_TIME");

                // 추출한 데이터로 TimerLogEntry 객체 생성
                TimerLogEntry entry = new TimerLogEntry(startTime, endTime, durationInSeconds);
                entries.add(entry);
            }
            rs.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return entries;
    }


    public int selectTotalTime(LocalDate date) {
        int totalStudyTime = 0; // 현재 총 시간

        // SQL 쿼리
        String query = "SELECT TOTAL_STUDYTIME FROM study_tb WHERE TODAY = ?";
        try {
            connection = dataSource.getConnection();
            // 매개변수 설정
            pstmt = connection.prepareStatement(query);
            pstmt.setDate(1, Date.valueOf(date));
            ResultSet rs = pstmt.executeQuery();
            // 결과가 존재하면 값을 읽음
            if (rs.next()) {
                totalStudyTime = rs.getInt("TOTAL_STUDYTIME");
            }
            rs.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return totalStudyTime;
    }

    public int getAllTimes() {
        int allTimes = 0;
        String query = "select sum(TOTAL_STUDYTIME) as TOTAL_TIMES from study_tb";
        try{
            connection = dataSource.getConnection();
            pstmt = connection.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                allTimes += rs.getInt("TOTAL_TIMES");
            }
            rs.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return allTimes;
    }

    public int getTodayTotalTime(LocalDate date) {
        int totalTime = 0;
        String query = "select TOTAL_STUDYTIME from study_tb where TODAY = ?";
        try{
            connection = dataSource.getConnection();
            pstmt = connection.prepareStatement(query);
            pstmt.setDate(1, Date.valueOf(date));
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()){
                totalTime = rs.getInt("TOTAL_STUDYTIME");
            }
            rs.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return totalTime;
    }
}
 