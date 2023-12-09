package com.sm.study_manager;

import java.net.URL;

import javafx.animation.FadeTransition;
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
    private Button cancelButton;    // 취소버튼
    @FXML
    private Button startButton;     // 시작버튼

    Map<Integer, String> numberMap; // 뭐에쓰는거지??
    Integer currSeconds;



    // Event handler for the start button
    @FXML
    private void start(ActionEvent event) {  // 시작
        scrollUp(); // 시작버튼 누르면 실행
    }



    // Event handler for the cancel button
    @FXML
    private void unStart(ActionEvent event) {    //취소
        scrollDown();

    }

    Integer hmsToSeconds(Integer h , Integer m , Integer s ) {
        Integer hToSeconds = h* 3600;
        Integer mToSecodns = m*60;
        Integer total = hToSeconds + mToSecodns + s;
        return total;
    }
    // 전부 초로 바꿔준다.

    // 5분50초 경 보면됨.
//    LinkedList<Integer> secondsdToHms(Integer currSeconds){
//        Integer h
//    }




    // 스크롤 애니메이션 할건가? 다른방식은없나?
    void scrollUp() {       //시작버튼 누르면실행 x가 가로니까 x를

        // 트랜스레이션 생성
        FadeTransition fadeOut = new FadeTransition(Duration.millis(100), menuPane);
//        TranslateTransition tr1 = new TranslateTransition();
//        tr1.setDuration(Duration.millis(100));
//        tr1.setToX(0);
//        tr1.setToY(-3000);  // 그냥화면밖으로 날려보냄
//        tr1.setToX(-1000);
//        tr1.setToY(0);
//        tr1.setNode(menuPane);

        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);


        FadeTransition fadeIn = new FadeTransition(Duration.millis(100), timerPane);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

//        TranslateTransition tr2 = new TranslateTransition();
//        tr2.setDuration(Duration.millis(100));
//        tr2.setFromX(0);
//        tr2.setFromY(200);
//        tr2.setToX(0);
//        tr2.setToY(0);
//        tr2.setNode(timerPane);
        ParallelTransition pt = new ParallelTransition(fadeOut, fadeIn);
        pt.play();
    }


    void scrollDown() {
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




