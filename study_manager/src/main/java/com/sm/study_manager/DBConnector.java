package com.sm.study_manager;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DBConnector {

        private static final String URL = "jdbc:mariadb://localhost:3306/STUDYDB";      // url 은 localhost에 studyDB 에 있음
        private static final String USER = "root";
        private static final String PASSWORD = "1234";

        public static Connection getConnection() throws SQLException {

            return DriverManager.getConnection(URL, USER, PASSWORD);
        }

        public static void insertLogEntryAndUpdateTotalTime(TimerLogEntry entry) {      // 로그에도 insert , total 데이터에도 insert
                String insertLogSql = "INSERT INTO timer_list_tb (STUDY_DATE, STUDY_START_TIME, STUDY_END_TIME, STUDY_JUST_TIME) VALUES (?, ?, ?, ?)";
                // timer_list_tb 에 저장
                String selectTotalSql = "SELECT TOTAL_STUDYTIME FROM study_tb WHERE TODAY = ?";
                // 오늘 총 공부시간 갖고옴
                String updateTotalSql = "UPDATE study_tb SET TOTAL_STUDYTIME = ? WHERE TODAY = ?";
                // 오늘 총 공부시간 업데이트
                String insertTotalSql = "INSERT INTO study_tb (TODAY, TOTAL_STUDYTIME) VALUES (?, ?)";
                // 오늘 총 공부시간에  추가
                Connection con = null;
                try {
                        con = getConnection();
                        con.setAutoCommit(false); // 트랜잭션 시작

                        // 로그 삽입
                        try (PreparedStatement logStmt = con.prepareStatement(insertLogSql)) {
                                logStmt.setDate(1, Date.valueOf(entry.getStartTime().toLocalDate()));
                                logStmt.setTimestamp(2, Timestamp.valueOf(entry.getStartTime()));
                                logStmt.setTimestamp(3, Timestamp.valueOf(entry.getEndTime()));
                                logStmt.setInt(4, entry.getDurationInSeconds());
                                logStmt.executeUpdate();
                        }

                        // 총 공부 시간 업데이트
                        try (PreparedStatement selectStmt = con.prepareStatement(selectTotalSql)) {
                                selectStmt.setDate(1, Date.valueOf(entry.getStartTime().toLocalDate()));
                                ResultSet rs = selectStmt.executeQuery();
                                int newTotalStudyTime = entry.getDurationInSeconds();

                                if (rs.next()) {
                                        newTotalStudyTime += rs.getInt("TOTAL_STUDYTIME");
                                        try (PreparedStatement updateStmt = con.prepareStatement(updateTotalSql)) {
                                                updateStmt.setInt(1, newTotalStudyTime);
                                                updateStmt.setDate(2, Date.valueOf(entry.getStartTime().toLocalDate()));
                                                updateStmt.executeUpdate();
                                        }
                                } else {
                                        try (PreparedStatement insertStmt = con.prepareStatement(insertTotalSql)) {
                                                insertStmt.setDate(1, Date.valueOf(entry.getStartTime().toLocalDate()));
                                                insertStmt.setInt(2, newTotalStudyTime);
                                                insertStmt.executeUpdate();
                                        }
                                }
                        }

                        con.commit(); // 트랜잭션 커밋
                } catch (SQLException e) {
                        e.printStackTrace();
                        if (con != null) {
                                try {
                                        con.rollback(); // 오류 발생 시 롤백
                                } catch (SQLException ex) {
                                        ex.printStackTrace();
                                }
                        }
                } finally {
                        if (con != null) {
                                try {
                                        con.close(); // 연결 종료
                                } catch (SQLException ex) {
                                        ex.printStackTrace();
                                }
                        }
                }
        }






        public static List<TimerLogEntry> selectLogEntriesByDate(LocalDate date) {
                List<TimerLogEntry> entries = new ArrayList<>();
                String sql = "SELECT * FROM timer_list_tb WHERE STUDY_DATE = ?";

                try (Connection con = getConnection();
                     PreparedStatement pstmt = con.prepareStatement(sql)) {

                        pstmt.setDate(1, Date.valueOf(date)); // SQL Date로 변환   // 현재날짜 매개변수로 sql 문에 보냄

                        try (ResultSet rs = pstmt.executeQuery()) {
                                while (rs.next()) {
                                        // 각 컬럼에서 데이터 추출
                                        LocalDateTime startTime = rs.getTimestamp("STUDY_START_TIME").toLocalDateTime();
                                        LocalDateTime endTime = rs.getTimestamp("STUDY_END_TIME").toLocalDateTime();
                                        int durationInSeconds = rs.getInt("STUDY_JUST_TIME");

                                        // 추출한 데이터로 TimerLogEntry 객체 생성
                                        TimerLogEntry entry = new TimerLogEntry(startTime, endTime, durationInSeconds);
                                        entries.add(entry);
                                }
                        }
                } catch (SQLException e) {
                        e.printStackTrace();
                }

                return entries;
        }

        public static int selectTotalTime(LocalDate date) {
                int totalStudyTime = 0; // 현재 총 시간

                // SQL 쿼리
                String query = "SELECT TOTAL_STUDYTIME FROM study_tb WHERE TODAY = ?";

                try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                     PreparedStatement pstmt = conn.prepareStatement(query)) {

                        // 매개변수 설정
                        pstmt.setDate(1, Date.valueOf(date));

                        try (ResultSet rs = pstmt.executeQuery()) {
                                // 결과가 존재하면 값을 읽음
                                if (rs.next()) {
                                        totalStudyTime = rs.getInt("TOTAL_STUDYTIME");
                                }
                        }
                } catch (SQLException e) {
                        e.printStackTrace();
                }

                return totalStudyTime;
        }



}

