package com.sm.study_manager;

import java.net.URL;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.util.Duration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;


import javafx.scene.layout.AnchorPane;

import com.sm.study_manager.TimerLogEntry;





public class TimerController extends CommonController implements Initializable {

    // db 연동


    @FXML
    private  Label totalStudyTime;
    // 오늘 공부한 총 시간이고,  흐르는 시간에 원래는 - 되지만 totalStudyTime 에 += 1
    @FXML
    private AnchorPane timerPane;

    @FXML
    private AnchorPane menuPane; // 시간정하는 판넬
    @FXML
    private Label hoursTimer;
    @FXML
    private Label minutesTimer;
    @FXML
    private Label secondsTimer; // 시간 가는거

    @FXML
    private ComboBox<Integer> hoursInput;
    @FXML
    private ComboBox<Integer> minutesInput;
    @FXML
    private ComboBox<Integer> secondsInput; // 이거는 고르는거.


    @FXML
    private Button timeStopButton;  // 일시정지 버튼

    @FXML
    private Button cancelButton;    // 취소버튼
    @FXML
    private Button startButton;     // 시작버튼

    @FXML
    private ListView timerLogView;  // 시작시간, 종료시간, 기록 하는데 필요한 리스트뷰
    Map<Integer, String> numberMap; // 뭐에쓰는거지??

    Thread thrd;
    Integer currSeconds;    // 현재 전체 입력한 시간몇초인지.

    Integer total = 0;  //

    Integer temp = 0;   // 이게 임시로 0으로 되야됨 한번 넣으면 그치

    private volatile boolean isPaused = false; // 일시정지 상태 추적

    private List<TimerLogEntry> logEntries = new LinkedList<>();    // 로그 타이머 시작시간, 종료시간

    private LocalDateTime currentStartTime; // 시작시간 기록
    // Event handler for the start button
    @FXML
    private void start(ActionEvent event) {  // 시작
        currSeconds = hmsToSeconds(hoursInput.getValue(), minutesInput.getValue() , secondsInput.getValue());
        // 현재 흘러야하는 초 값넣은것 다 갖고와서 바꿔주기

        hoursInput.setValue(0);
        minutesInput.setValue(0);
        secondsInput.setValue(0);

        // 인풋값 0으로 초기화

        currentStartTime = LocalDateTime.now(); // 시작 시간 기록

        scrollUp(); // 시작버튼 누르면 실행
    }



    void startCountdown() {
        thrd = new Thread(() -> {
            try {
                while (currSeconds > 0) {
                    if (!isPaused) {
                        Platform.runLater(this::setOutput);
                        currSeconds--; // 먼저 감소
                        temp++;
                    }
                    Thread.sleep(1000); // 그 후에 대기
                }
                Platform.runLater(() -> { // 0에 도달하면
                    updateTotalStudyTimeLabel(); // 흐른 시간 업데이트
                    temp = 0; // 임시 시간 초기화
                    scrollDown(); // 다운 스크롤
                });
            } catch (InterruptedException e) {
                System.out.println("스레드가 중단됨: " + e);
                Thread.currentThread().interrupt(); // 스레드 인터럽트 상태를 설정
            }
        });
        thrd.start();
    }


    void updateTotalStudyTimeLabel() {

        total += temp;
        Integer writeTemp = temp;   // 기록용

        // totalStudyTime 값을 사용하여 라벨 업데이트
        // 예: totalStudyTimeLabel.setText("총 공부 시간: " + totalStudyTime + "초");
        totalStudyTime.setText(total + "초");

        LocalDateTime endTime = LocalDateTime.now(); // 종료 시간 기록
        logEntries.add(new TimerLogEntry(currentStartTime, endTime, temp)); // 여기에서 temp를 전달합니다.

        // temp = 흐른 초 까지 전달

        updateLogDisplay();     // 리스트뷰에 업데이트 ..
    }

    private void updateLogDisplay() {
        // 포맷터 정의 (소수점 이하 3자리까지 표현)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        timerLogView.getItems().clear(); // 기존 로그를 클리어
        for (TimerLogEntry entry : logEntries) {
            String formattedStartTime = entry.getStartTime().format(formatter);
            String formattedEndTime = entry.getEndTime().format(formatter);

//            // 여기에서 temp를 시, 분, 초로 변환
            int hours = entry.getDurationInSeconds() / 3600;
            int minutes = (entry.getDurationInSeconds() % 3600) / 60;
            int seconds = entry.getDurationInSeconds() % 60;

            // 포맷된 공부 시간 문자열 생성
            String studyDurationFormatted = String.format("%d시간 %d분 %d초", hours, minutes, seconds);

            // 로그 텍스트에 공부 시간을 추가
            String logText = "시작시간: " + formattedStartTime + ", 종료시간: " + formattedEndTime + ", 공부시간: " + studyDurationFormatted;
            timerLogView.getItems().add(logText); // 포맷된 텍스트를 추가
        }
    }

    // 이건 왜?
    void setOutput() {
        LinkedList<Integer> currHms = secondsToHms(currSeconds);    // 현재입력받은 것인데 linkedList 로 시분초 012 ㅇㅇ

        System.out.println(currHms.get(0) + "-" + currHms.get(1) + " - " + currHms.get(2)); // 이거 왜쓰는거지

        hoursTimer.setText(numberMap.get(currHms.get(0)));
        minutesTimer.setText(numberMap.get(currHms.get(1)));
        secondsTimer.setText(numberMap.get(currHms.get(2)));


    }

    @FXML
    private void unStart(ActionEvent event) {
        // 사용자에게 타이머를 정말로 취소할지 확인을 요청하는 대화 상자를 생성합니다.
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("타이머를 취소하시겠습니까?");
        alert.setHeaderText("지금까지 공부한 시간이 누적되지않습니다.");
        alert.setContentText("일시정지를 눌러 휴식하세요");

        // 대화 상자를 보여주고 사용자의 응답을 기다립니다.
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // 사용자가 '예'를 선택한 경우, 타이머를 취소하고 temp 값을 초기화합니다.
            if (thrd != null) {
                thrd.interrupt(); // 스레드를 안전하게 중단합니다.
                thrd = null; // 스레드를 null로 설정하여 GC가 수집할 수 있도록 합니다.
            }
            temp = 0; // temp 값을 초기화합니다.
            scrollDown(); // 스크롤 다운 메서드를 호출합니다.
        }
    }

    @FXML
    private void toggleTime(ActionEvent event) {
        isPaused = !isPaused; // 일시정지 상태를 전환
    }


    Integer hmsToSeconds(Integer h , Integer m , Integer s ) {
        Integer hToSeconds = h* 3600;
        Integer mToSecodns = m*60;
        Integer total = hToSeconds + mToSecodns + s;
        return total;
    }
    // 전부 초로 바꿔준다.

    LinkedList<Integer> secondsToHms(Integer currSeconds){
        Integer hours = currSeconds / 3600;
        currSeconds = currSeconds % 3600;
        Integer minutes = currSeconds /60;
        currSeconds = currSeconds % 60;
        Integer seconds = currSeconds;
        LinkedList<Integer> answer = new LinkedList<>();
        answer.add(hours);
        answer.add(minutes);
        answer.add(seconds);
        return answer;
    }




    // 스크롤 애니메이션 할건가? 다른방식은없나?
    void scrollUp() {       //시작버튼 누르면실행 x가 가로니까 x를

        // 트랜스레이션 생성
//        FadeTransition fadeOut = new FadeTransition(Duration.millis(100));
        TranslateTransition tr1 = new TranslateTransition();
        tr1.setDuration(Duration.millis(100));
        tr1.setToX(0);
        tr1.setToY(-3000);  // 그냥화면밖으로 날려보냄
        tr1.setToX(-1000);
        tr1.setToY(0);
        tr1.setNode(menuPane);

//        fadeOut.setFromValue(1.0);
//        fadeOut.setToValue(0.0);
//        fadeOut.setNode(menuPane);

//        FadeTransition fadeIn = new FadeTransition(Duration.millis(100));
//        fadeIn.setFromValue(0.0);
//        fadeIn.setToValue(1.0);
//        fadeOut.setNode(timerPane);


        TranslateTransition tr2 = new TranslateTransition();
        tr2.setDuration(Duration.millis(100));
        tr2.setFromX(0);
        tr2.setFromY(200);
        tr2.setToX(0);
        tr2.setToY(0);
        tr2.setNode(timerPane);
        ParallelTransition pt = new ParallelTransition(tr1, tr2);
        pt.setOnFinished(e->{
            try {
                System.out.println("Start Countdown");
                startCountdown();   // 여기서 스레드시작

            }catch(Exception e2){
                //

            }
        });
        pt.play();
    }


    void scrollDown() {
        // 트랜스레이션 생성
        FadeTransition fadeOut = new FadeTransition(Duration.millis(100), menuPane);

        TranslateTransition tr1 = new TranslateTransition();
        tr1.setDuration(Duration.millis(100));
        tr1.setToX(-1000);
        tr1.setToY(0);
        tr1.setNode(timerPane);

        TranslateTransition tr2 = new TranslateTransition();
        tr2.setDuration(Duration.millis(100));
        tr2.setFromX(0);
        tr2.setFromY(246);
        tr2.setToX(0);
        tr2.setToY(0);
        tr2.setNode(menuPane);
        ParallelTransition pt = new ParallelTransition(tr1, tr2);
//        pt.setOnFinished(e->{
//            try {
//                System.out.println("Start Countdown");
//                thrd.stop();
//
//            }catch(Exception e2){
//                System.out.println("에러" + e2);
//
//            }
//        });
        pt.play();

    }




    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<Integer> hoursList = FXCollections.observableArrayList();
        ObservableList<Integer> minutesAndSecondsList = FXCollections.observableArrayList();

        for(int i = 0; i<=60 ; i++){
            if(0 <= i && i <=24) {
                hoursList.add(Integer.valueOf(i));
            }
            minutesAndSecondsList.add(Integer.valueOf(i));
        }

        hoursInput.setItems(hoursList);
        hoursInput.setValue(0);

        minutesInput.setItems(minutesAndSecondsList);
        minutesInput.setValue(0);

        secondsInput.setItems(minutesAndSecondsList);
        secondsInput.setValue(0);

        // 이런식으로 넣음 0~24 , 0~60 0~60

        numberMap = new TreeMap<Integer, String>();
        for (Integer i = 0; i<=60 ; i++) {
            if(0<=i && i <=9) {
                numberMap.put(i, "0" + i.toString());
            } else {
                numberMap.put(i, i.toString());
            }
        }
        // tree Map? <왜 문자열이들어가지?>
    }

}




