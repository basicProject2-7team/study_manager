package com.sm.study_manager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.util.function.Function;

public class CalenderController extends CommonController implements Initializable {
    // 달력을 표시할 그리드 입니다.
    @FXML
    private GridPane grid;

    @FXML
    private Label allTimesLabel;

    @FXML
    private Label selectedDate;

    @FXML
    private Label studyTime;

    @FXML
    private ListView studyList;

    // 날자를 표시할 라벨 입니다.
    @FXML
    private Label lbl00, lbl01, lbl02, lbl03, lbl04, lbl05, lbl06, lbl10, lbl11, lbl12, lbl13, lbl14, lbl15, lbl16,
            lbl20, lbl21, lbl22, lbl23, lbl24, lbl25, lbl26, lbl30, lbl31, lbl32, lbl33, lbl34, lbl35, lbl36, lbl40,
            lbl41, lbl42, lbl43, lbl44, lbl45, lbl46, lbl50, lbl51, lbl52, lbl54, lbl55, lbl56, lbl53;

    // 라벨과 hbox를 배열로 정합니다.
    public Label[] labelList;
    public HBox[] hboxList;

    // 현재 년도와 달을 표시하는 버튼과 현재의 달로 바로 돌아오는 버튼 입니다.
    @FXML
    private Button btnToday;

    // 현재의 년과 월을 구합니다.
    private LocalDateTime date = LocalDateTime.now();
    int currentYear = date.getYear();
    int currentMonthInt = date.getMonthValue();

    // 월의 첫번째 요일과 마지막 요일을 저장할부분
    YearMonth firstAndLastDay;

    // 색 비율 (초 단위)
    double persent;

    // 이전 달과 다음 달의 날짜 인덱스를 저장할 리스트
    private List<GridIndex> previousMonthGridIndices;
    private List<GridIndex> nextMonthGridIndices;

    private DBConnector dbConnector = new DBConnector();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 라벨을 리스트로 만들어 날자를 표시할때 사용합니다.
        labelList = new Label[] { lbl00, lbl01, lbl02, lbl03, lbl04, lbl05, lbl06, lbl10, lbl11, lbl12, lbl13, lbl14,
                lbl15, lbl16, lbl20, lbl21, lbl22, lbl23, lbl24, lbl25, lbl26, lbl30, lbl31, lbl32, lbl33, lbl34, lbl35,
                lbl36, lbl40, lbl41, lbl42, lbl43, lbl44, lbl45, lbl46, lbl50, lbl51, lbl52, lbl53, lbl54, lbl55,
                lbl56 };

        previousMonthGridIndices = new ArrayList<>();
        nextMonthGridIndices = new ArrayList<>();

        int currentDay = LocalDate.now().getDayOfMonth();
        LocalDate currentDate = LocalDate.of(currentYear, currentMonthInt, currentDay);
        settingLabel(currentDate);

        fillUpCalendar(currentYear, currentMonthInt);
        String strYearMonth = currentYear + "년" + currentMonthInt + "월";
        btnToday.setText(strYearMonth);
        for (Label label : labelList) {
            if (label.getText().equals(String.valueOf(currentDay))) {
                String currentStyle = label.getStyle();
                label.setStyle(currentStyle + "; -fx-border-color: red;");
                return;
            }
        }
    }

    @FXML
    void bMonthClick(ActionEvent event) {//이전달을 표시하는 부분

        if (currentMonthInt == 1) {
            currentYear--;
            currentMonthInt = 12;
        } else {
            currentMonthInt--;
        }

        fillUpCalendar(currentYear, currentMonthInt);
        String strYearMonth = currentYear + "년" + currentMonthInt + "월";
        btnToday.setText(strYearMonth);

    }

    @FXML
    void nMonthClick(ActionEvent event) {//다음달을 표시하는 부분

        if (currentMonthInt == 12) {
            currentYear++;
            currentMonthInt = 1;
        } else {
            currentMonthInt++;
        }

        fillUpCalendar(currentYear, currentMonthInt);
        String strYearMonth = currentYear + "년" + currentMonthInt + "월";
        btnToday.setText(strYearMonth);
    }

    @FXML
    private void clickGrid(MouseEvent event) {
        clearLabelBorder();
        Label clickedLabel = null;
        String clickedDate;
        Node clickedNode = null;

        // 클릭된 노드를 기준으로 GridPane 내 위치를 찾기 위해 상위 노드 탐색
        clickedNode = (Node) event.getTarget();
        while (clickedNode != null && !(clickedNode.getParent() instanceof GridPane)) {
            clickedNode = clickedNode.getParent();
        }

        // 클릭된 노드가 Label, VBox의 자식 노드인 Label, 또는 Text인 경우
        Node targetNode = (Node) event.getTarget();
        if (targetNode instanceof Label) {
            clickedLabel = (Label) targetNode;
        } else if (targetNode instanceof VBox) {
            VBox clickedVBox = (VBox) targetNode;
            if (!clickedVBox.getChildren().isEmpty() && clickedVBox.getChildren().get(0) instanceof Label) {
                clickedLabel = (Label) clickedVBox.getChildren().get(0);
            }
        } else if (targetNode instanceof Text) {
            if (targetNode.getParent() instanceof Label) {
                clickedLabel = (Label) targetNode.getParent();
            }
        }

        Integer rowIndex = GridPane.getRowIndex(clickedNode);
        Integer colIndex = GridPane.getColumnIndex(clickedNode);
        int row = rowIndex != null ? rowIndex : 0;
        int col = colIndex != null ? colIndex : 0;

        // 클릭된 Label의 스타일을 변경하고 날짜를 출력
        if (clickedLabel != null) {
            String currentStyle = clickedLabel.getStyle();
            String backgroundColorStyle = extractBackgroundColorStyle(currentStyle);
            clickedLabel.setStyle(backgroundColorStyle + "; -fx-border-color: red;");
            clickedDate = clickedLabel.getText();
            System.out.println("Clicked date: " + clickedDate);

            int clickedDay = Integer.parseInt(clickedDate);
            LocalDate clickedLabelDate;

            if (previousMonthGridIndices.contains(new GridIndex(row, col))) {
                clickedLabelDate = firstAndLastDay.minusMonths(1).atDay(clickedDay);
            } else if (nextMonthGridIndices.contains(new GridIndex(row, col))) {
                clickedLabelDate = firstAndLastDay.plusMonths(1).atDay(clickedDay);
            } else {
                clickedLabelDate = firstAndLastDay.atDay(clickedDay);
            }

            settingLabel(clickedLabelDate);

            // 최종적으로 찾은 GridPane 내 위치의 행과 열을 출력
            if (clickedNode != null) {
                if(previousMonthGridIndices.contains(new GridIndex(row, col))){
                    bMonthClick(null);
                    for (Label label : labelList) {
                        if (label.getText().equals(clickedDate)) {
                            label.setStyle("-fx-border-color: red;"); // 예시
                            return;
                        }
                    }
                } else if (nextMonthGridIndices.contains(new GridIndex(row, col))){
                    nMonthClick(null);
                    for (Label label : labelList) {
                        if (label.getText().equals(clickedDate)) {
                            label.setStyle("-fx-border-color: red;"); // 예시
                            return;
                        }
                    }
                }
            }
        }
    }

    public void settingLabel(LocalDate clickedLabelDate){
        int year = clickedLabelDate.getYear();
        int month = clickedLabelDate.getMonthValue();
        int day = clickedLabelDate.getDayOfMonth();
        int week = clickedLabelDate.getDayOfWeek().getValue();
        int totalTime = dbConnector.selectTotalTime(clickedLabelDate);
        int allTimes = dbConnector.getAllTimes();
        // 1초/모든시간
        persent = 1.0/allTimes;

        getCurrentInfo(clickedLabelDate);
        allTimesLabel.setText(formatDuration(allTimes));
        selectedDate.setText(year+"년 "+month+"월 "+day+"일 ["+getWeek(week)+"]");
        studyTime.setText(formatDuration(totalTime));
    }

    public String getWeek(int num) {
        switch (num){
            case 7 :
                return "일요일";
            case 1 :
                return "월요일";
            case 2 :
                return "화요일";
            case 3 :
                return "수요일";
            case 4 :
                return "목요일";
            case 5 :
                return "금요일";
            case 6 :
                return "토요일";
        }
        return "";
    }

    public String formatDuration(int totalSeconds) {
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;
        return String.format("%d시간 %d분 %d초", hours, minutes, seconds);
    }

    public void getCurrentInfo(LocalDate clickedLabelDate){
        List<TimerLogEntry> list = dbConnector.selectLogEntriesByDate(clickedLabelDate);

        ObservableList<String> logList = FXCollections.observableArrayList();
        for(TimerLogEntry timerLogEntry : list) {
            String startTime = timerLogEntry.getStartTime().toString();
            String endTime = timerLogEntry.getEndTime().toString();
            String duration = formatDuration(timerLogEntry.getDurationInSeconds());
            String logText = String.format("시작: %s, 종료: %s, 지속: %s", startTime, endTime, duration);
            logList.add(logText);
        }

        studyList.setItems(logList);
    }


    @FXML
    private void todayClick(ActionEvent event) {
        currentYear = date.getYear();
        currentMonthInt = date.getMonthValue();
        clearLabel();
        fillUpCalendar(currentYear, currentMonthInt);
        String strYearMonth = currentYear + "년" + currentMonthInt + "월";
        btnToday.setText(strYearMonth);
    }

    private void clearLabel() {
        for (Label label : labelList) {
            label.setStyle("-fx-text-fill: black;");
        }
    }

    private void clearLabelBorder() {
        for (Label label : labelList) {
            String currentStyle = label.getStyle();
            String backgroundColorStyle = extractBackgroundColorStyle(currentStyle);
            Paint textColor = label.getTextFill();
            label.setStyle(backgroundColorStyle + "; -fx-border-color: transparent; -fx-text-fill: "+convertToHexColor(textColor)+";");
        }
    }

    // 배경색 스타일을 추출하는 메소드
    private String extractBackgroundColorStyle(String style) {
        if (style == null) {
            return "";
        }

        // '-fx-background-color' 속성 찾기
        int index = style.indexOf("-fx-background-color");
        if (index != -1) {
            int endIndex = style.indexOf(";", index);
            if (endIndex != -1) {
                // '-fx-background-color' 속성 값 추출
                return style.substring(index, endIndex);
            } else {
                // 스타일 문자열의 끝까지 '-fx-background-color' 속성 값 추출
                return style.substring(index);
            }
        }

        return "";
    }

    private String convertToHexColor(Paint paint) {
        if (paint instanceof Color) {
            Color color = (Color) paint;
            return "#" + color.toString().substring(2, 8);
        }
        return null;
    }

    private void fillUpCalendar(int currentYear, int currentMonthInt) {
        clearLabel();
        previousMonthGridIndices.clear();
        nextMonthGridIndices.clear();

        // 현재 년도와 달에 해당하는 YearMonth 객체 생성
        YearMonth yearMonth = YearMonth.of(currentYear, currentMonthInt);

        // 월의 첫번째 날과 마지막 날의 요일 구하기
        LocalDate firstDayOfMonth = yearMonth.atDay(1); // 월 1일 요일
        LocalDate lastDayOfMonth = yearMonth.atEndOfMonth(); // 월 마지막 일 요일
        DayOfWeek firstWeekDay = firstDayOfMonth.getDayOfWeek(); // 1일
        DayOfWeek lastWeekDay = lastDayOfMonth.getDayOfWeek(); // 마지막 일
        firstAndLastDay = YearMonth.of(currentYear, currentMonthInt); // 월의 시작일, 마지막 일

        // 첫번째 날의 인덱스 계산
        int firstIndex = firstWeekDay.getValue() % 7;

        // 이전 달의 마지막 날 채우기
        YearMonth previousMonth = yearMonth.minusMonths(1); // 이전 달의 YearMonth 객체 생성
        int lastDayOfPreviousMonth = previousMonth.lengthOfMonth(); // 이전 달의 마지막 일

        // 이전 달의 날짜 인덱스 추가
        for (int i = 0; i < firstIndex; i++) {
            previousMonthGridIndices.add(new GridIndex(0, i));
        }

        for (int i = firstIndex - 1; i >= 0; i--) {
            labelList[i].setText(String.valueOf(lastDayOfPreviousMonth));
            labelList[i].setStyle("-fx-text-fill: grey;");
            lastDayOfPreviousMonth--;
        }

        // 이번 달 날짜 표시
        int day = 1;
        for (int i = firstIndex; i < firstIndex + yearMonth.lengthOfMonth(); i++) {
            setBackgroundColors(yearMonth, day, labelList[i]);
            labelList[i].setText(String.valueOf(day));
            day++;
        }

        // 다음 달의 첫째 주 채우기
        int nextMonthFillCount = 0; // 다음 달의 첫째 주를 채울 개수
        int remainingDays = 42 - (firstIndex + yearMonth.lengthOfMonth()); // 남은 셀 개수

        if (remainingDays > 0) {
            YearMonth nextMonth = yearMonth.plusMonths(1); // 다음 달의 YearMonth 객체 생성
            LocalDate firstDayOfNextMonth = nextMonth.atDay(1); // 다음 달의 첫째 날

            // 다음 달의 날짜 인덱스 추가
            int startIndex = firstIndex + yearMonth.lengthOfMonth();
            for (int i = startIndex; i < 42; i++) {
                int row = i / 7;
                int col = i % 7;
                nextMonthGridIndices.add(new GridIndex(row, col));
            }

            for (int i = firstIndex + yearMonth.lengthOfMonth(); i < 42; i++) {
                labelList[i].setText(String.valueOf(firstDayOfNextMonth.getDayOfMonth() + nextMonthFillCount));
                labelList[i].setStyle("-fx-text-fill: grey;");
                nextMonthFillCount++;
            }
        }
    }

    public void setBackgroundColors(YearMonth yearMonth, int day, Label label) {
        LocalDate date = yearMonth.atDay(day);
        int todayTotalTime = dbConnector.getTodayTotalTime(date);
        label.setStyle("-fx-background-color: rgb(0, 255, 0, "+todayTotalTime*persent+")");
    }

}
