package com.sm.study_manager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class CalenderController extends CommonController implements Initializable {
    // 달력을 표시할 그리드 입니다.
    @FXML
    private GridPane grid;

    // 일자별 데이터를 표시할 HBox 입니다.
    @FXML
    private HBox hbox1, hbox2, hbox3, hbox4, hbox5, hbox6, hbox7, hbox8, hbox9, hbox10, hbox11, hbox12, hbox13, hbox14,
            hbox15, hbox16, hbox17, hbox18, hbox19, hbox20, hbox21, hbox22, hbox23, hbox24, hbox25, hbox26, hbox27,
            hbox28, hbox29, hbox30, hbox31, hbox32, hbox33, hbox34, hbox35, hbox36, hbox37, hbox38, hbox39;

    // 날자를 표시할 라벨 입니다.
    @FXML
    private Label lbl00, lbl01, lbl02, lbl03, lbl04, lbl05, lbl06, lbl10, lbl11, lbl12, lbl13, lbl14, lbl15, lbl16,
            lbl20, lbl21, lbl22, lbl23, lbl24, lbl25, lbl26, lbl30, lbl31, lbl32, lbl33, lbl34, lbl35, lbl36, lbl40,
            lbl41, lbl42, lbl43, lbl44, lbl45, lbl46, lbl50, lbl51, lbl52, lbl54, lbl55, lbl56, lbl53;

    // 라벨과 hbox를 배열로 정합니다.
    public Label[] labelList;
    public HBox[] hboxList;

    // 월단위를 뒤로 가는 버튼
    @FXML
    private Button btnBMonth;

    // 현재 년도와 달을 표시하는 버튼과 현재의 달로 바로 돌아오는 버튼 입니다.
    @FXML
    private Button btnToday;

    // 월단위를 앞으로 가는 버튼
    @FXML
    private Button btnNMonth;

    // 이전달의 일 수
    private int previousMonthDays;

    // 다음달의 일 수
    private int nextMonthDays;

    // 현재의 년과 월을 구합니다.
    private LocalDateTime date = LocalDateTime.now();
    int currentYear = date.getYear();
    int currentMonthInt = date.getMonthValue();

    // 월의 첫번째 요일과 마지막 요일을 저장할부분
    YearMonth firstAndLastDay;
    // 첫번째날의 요일
    String strFirstWeek;
    // 마지막날의 요일
    String strLastWeek;
    // -----------------날짜의 칸을 클릭했을때 값을 받아오는
    String clickDate;

    // 오늘날짜를 깜빡이게 하고 멈추게 하기 위해서 사용합니다.
    public Boolean booStopBlink = true;

    // 이전 달과 다음 달의 날짜 인덱스를 저장할 리스트
    private List<GridIndex> previousMonthGridIndices;
    private List<GridIndex> nextMonthGridIndices;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // hbox를 리스트로 만들어 일자별 데이터를 표시할때 사용합니다.
        hboxList = new HBox[] { hbox1, hbox2, hbox3, hbox4, hbox5, hbox6, hbox7, hbox8, hbox9, hbox10, hbox11, hbox12,
                hbox13, hbox14, hbox15, hbox16, hbox17, hbox18, hbox19, hbox20, hbox21, hbox22, hbox23, hbox24, hbox25,
                hbox26, hbox27, hbox28, hbox29, hbox30, hbox31, hbox32, hbox33, hbox34, hbox35, hbox36, hbox37, hbox38,
                hbox39 };

        // 라벨을 리스트로 만들어 날자를 표시할때 사용합니다.
        labelList = new Label[] { lbl00, lbl01, lbl02, lbl03, lbl04, lbl05, lbl06, lbl10, lbl11, lbl12, lbl13, lbl14,
                lbl15, lbl16, lbl20, lbl21, lbl22, lbl23, lbl24, lbl25, lbl26, lbl30, lbl31, lbl32, lbl33, lbl34, lbl35,
                lbl36, lbl40, lbl41, lbl42, lbl43, lbl44, lbl45, lbl46, lbl50, lbl51, lbl52, lbl53, lbl54, lbl55,
                lbl56 };

        previousMonthGridIndices = new ArrayList<>();
        nextMonthGridIndices = new ArrayList<>();

        fillUpCalendar(currentYear, currentMonthInt);
        String strYearMonth = currentYear + "년" + currentMonthInt + "월";
        btnToday.setText(strYearMonth);
    }

    @FXML
    void bMonthClick(ActionEvent event) {//이전달을 표시하는 부분

        booStopBlink = false;// 달이 변경되면 블링크를 멈춘다.

        if (currentMonthInt == 1) {
            currentYear--;
            currentMonthInt = 12;
        } else {
            currentMonthInt--;
        }

        // 현재의 달력이 현재의 년과 달이 같으면 깜빡임
        if (date.getYear() == currentYear && date.getMonthValue() == currentMonthInt) {
            booStopBlink = true;
        }

        fillUpCalendar(currentYear, currentMonthInt);
        String strYearMonth = currentYear + "년" + currentMonthInt + "월";
        btnToday.setText(strYearMonth);

    }

    @FXML
    void nMonthClick(ActionEvent event) {//다음달을 표시하는 부분

        booStopBlink = false;// 달이 변경되면 블링크를 멈춘다.

        if (currentMonthInt == 12) {
            currentYear++;
            currentMonthInt = 1;
        } else {
            currentMonthInt++;
        }

        // 현재의 달력이 현재의 년과 달이 같으면 깜빡임
        if (date.getYear() == currentYear && date.getMonthValue() == currentMonthInt) {
            booStopBlink = true;
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

        // 최종적으로 찾은 GridPane 내 위치의 행과 열을 출력
        if (clickedNode != null) {

            if(previousMonthGridIndices.contains(new GridIndex(row, col))){
                bMonthClick(null);
            } else if (nextMonthGridIndices.contains(new GridIndex(row, col))){
                nMonthClick(null);
            }
        }

        // 클릭된 Label의 스타일을 변경하고 날짜를 출력
        if (clickedLabel != null) {
            clickedLabel.setStyle("-fx-border-color: red;");
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

            System.out.println("format 전 : " + clickedLabelDate);
            // 날짜를 "YYYY-MM-DD" 형식으로 출력
            String formattedDate = clickedLabelDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
            System.out.println("Clicked date: " + formattedDate);
        }
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
            Paint labelTextColor = label.getTextFill();
            label.setStyle("-fx-text-fill: " + convertToHexColor(labelTextColor) + ";");
        }
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
        // 첫번째 날의 요일과 마지막 날의 요일을 문자열로 저장
        String strFirstWeek = firstWeekDay.getDisplayName(TextStyle.SHORT, Locale.getDefault());
        String strLastWeek = lastWeekDay.getDisplayName(TextStyle.SHORT, Locale.getDefault());

        // 첫번째 날의 인덱스 계산
        int firstIndex = firstWeekDay.getValue() % 7;

        // 이전 달의 마지막 날 채우기
        YearMonth previousMonth = yearMonth.minusMonths(1); // 이전 달의 YearMonth 객체 생성
        int lastDayOfPreviousMonth = previousMonth.lengthOfMonth(); // 이전 달의 마지막 일
        previousMonthDays = firstIndex; // 이전 달의 마지막 날을 채울 개수

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
            nextMonthDays = nextMonthFillCount;
        }
    }
}
