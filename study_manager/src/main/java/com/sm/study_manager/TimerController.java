package com.sm.study_manager;

import java.net.URL;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.util.Duration;

import java.util.LinkedList;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;

import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;



import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;


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

    Map<Integer, String> numberMap; // 뭐에쓰는거지??

    Thread thrd;
    Integer currSeconds;    // 현재 전체 입력한 시간몇초인지.

    Integer total = 0;  //

    Integer temp = 0;   // 이게 임시로 0으로 되야됨 한번 넣으면 그치

    private volatile boolean isPaused = false; // 일시정지 상태 추적

    // Event handler for the start button
    @FXML
    private void start(ActionEvent event) {  // 시작
        currSeconds = hmsToSeconds(hoursInput.getValue(), minutesInput.getValue() , secondsInput.getValue());
        hoursInput.setValue(0);
        minutesInput.setValue(0);
        secondsInput.setValue(0);

        scrollUp(); // 시작버튼 누르면 실행
    }


//    void startCountdown() {
//        thrd = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    while(currSeconds >= 0) {
//                        Platform.runLater(() -> setOutput()); // UI 업데이트를 FX 스레드에서 실행
//                        Thread.sleep(1000);
//                        currSeconds -= 1;
//                        temp += 1; // 경과 시간 증가 일단 총 시간
//
//
//                    }
//
//                        Platform.runLater(() -> {
//                            updateTotalStudyTimeLabel();
//                            temp = 0;   // 임시로 지금 흐른시간 0 초로 다시돌려줌.
//                            scrollDown(); // FX 스레드에서 UI 업데이트
//                            thrd.stop();
//                            System.out.println("피니시");
//                        });
//
//
//                } catch (InterruptedException e) {
//                    System.out.println("스레드가 중단됨: " + e);
//                } catch (Exception e) {
//                    System.out.println("오류: " + e);
//                }
//            }
//        });
//        thrd.start();
//    }

    void startCountdown() {
        thrd = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while(currSeconds >= 0) {
                        if (!isPaused) {    // 처음에 false 였다가 누르면 true 라서 여기 진행안됨.
                            Platform.runLater(() -> setOutput());
                            currSeconds -= 1;
                            temp += 1;
                        }
                        Thread.sleep(1000); // 항상 1초마다 잠시 멈춥니다.
                    }
                    Platform.runLater(() -> {   // 시간이 0보다작아지면
                        updateTotalStudyTimeLabel();     // 흐른시간을 위 라벨에 더해주고
                        temp = 0;   // 타이머 흐른 시간 0 으로초기화
                        scrollDown();   // 시간 정하는 화면으로 전환
                    });
                } catch (InterruptedException e) {
                    System.out.println("스레드가 중단됨: " + e);
                }
            }
        });
        thrd.start();
    }
    void updateTotalStudyTimeLabel() {
        total += temp;
        // totalStudyTime 값을 사용하여 라벨 업데이트
        // 예: totalStudyTimeLabel.setText("총 공부 시간: " + totalStudyTime + "초");
        totalStudyTime.setText(total + "초");
    }

    // 이건 왜?
    void setOutput() {
        LinkedList<Integer> currHms = secondsToHms(currSeconds);    // 현재입력받은 것인데 linkedList 로 시분초 012 ㅇㅇ

        System.out.println(currHms.get(0) + "-" + currHms.get(1) + " - " + currHms.get(2)); // 이거 왜쓰는거지

        hoursTimer.setText(numberMap.get(currHms.get(0)));
        minutesTimer.setText(numberMap.get(currHms.get(1)));
        secondsTimer.setText(numberMap.get(currHms.get(2)));


    }


    // Event handler for the cancel button
    @FXML
    private void unStart(ActionEvent event) {    //취소
        thrd.stop();    // 스레드멈춤.
        scrollDown();


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

//     5분50초 경 보면됨.
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




