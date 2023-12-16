package com.sm.study_manager;

import java.io.IOException;
import java.net.URL;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.time.LocalDate;
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

//import javax.print.attribute.standard.Media;


import com.sm.study_manager.TimerStartModalController;  // 모달 창 컨트롤러 import 해줘야하나?


public class TimerController extends CommonController implements Initializable {

    @FXML
    private Label totalStudyTime;
    // 오늘 공부한 총 시간이고,  흐르는 시간에 원래는 - 되지만 totalStudyTime 에 += 1

    // 초를 시 분 초로 나오게 해줘야함.
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

    Thread thrd;    // 시간 흐르는 스레드
    Integer currSeconds=0;    // 현재 전체 입력한 시간몇초인지.

    Integer total = 0;  //

    Integer temp = 0;   // 이게 임시로 0으로 되야됨 한번 넣으면 그치

    private volatile boolean isPaused = false; // 일시정지 상태 추적 첨 false 니까 faslse 가 재생 // true 가 멈춤

    private List<TimerLogEntry> logEntries = new LinkedList<>();    // 로그 타이머 시작시간, 종료시간

    private LocalDateTime currentStartTime; // 시작시간 기록
    // Event handler for the start button

    // 클래스 멤버 변수로 MediaPlayer 추가
    TimerStartModalController Modalcontroller;
    private MediaPlayer mediaPlayer;

    private DBConnector dbConnector = new DBConnector();

    // 종료버튼눌러 종료하거나, 0이되거나 하면 음악이 종료되고
    // 일시정지 버튼을 누르면 stop() 했다가 다시 실행되게 해야함.



    private static boolean isTimerRunningStatic = false;
    @FXML
    private void start(ActionEvent event) {  // 시작버튼 이벤트 핸들러
        if (!isTimerRunningStatic) {
            currSeconds = hmsToSeconds(hoursInput.getValue(), minutesInput.getValue(), secondsInput.getValue());
            hoursInput.setValue(0);
            minutesInput.setValue(0);
            secondsInput.setValue(0);
            showModalWindow();
            currentStartTime = LocalDateTime.now();
            scrollUp();
            startCountdown();

            // 그후에
            this.mediaPlayer = Modalcontroller.getMediaPlayer();
            isTimerRunningStatic = true;
        }


        // mediaPlayer 가 생겨서 가져옴 /
    }

    // 모달창 띄워주는 함수이고요
    //TimerStartModalView.fxml
    private void showModalWindow() {
        try {
            // 모달 창 FXML 파일 로드
//            모달 창을 여러 번 열고 닫을 때마다 FXMLLoader를 새로 생성하고 있는건가요??
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("TimerStartModalView.fxml")); // 음악선택뷰
            Parent parent = fxmlLoader.load();  // 이건왜하는거지?
            // 이 호출이 컨트롤러 인스턴스를 생성합니다.

            // 모달 창을 표시하기 전에 mediaPlayer를 TimerStartModalController에서 가져옵니다.
            Modalcontroller = fxmlLoader.getController();    // 전역변수

            System.out.println("controller 잘갖고왔는지?" + Modalcontroller);// controller 잘갖고왔는지?

            // 새로운 Scene 생성
            Scene scene = new Scene(parent);

            // 새로운 Stage(모달 창) 생성
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL); // 모달 창으로 설정
            stage.setTitle("음악 선택창"); // 모달 창의 제목 설정
            stage.setScene(scene); // Stage에 Scene 설정   이런식으로 씬을 갖고옴 fxml 파일
            stage.showAndWait(); // 모달 창을 표시하고 사용자의 입력을 기다림
            // 다른 밑에 컴포넌트 사용 못하게.
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 숫자1 씩 줄어듦 스레드로
    void startCountdown() {
        thrd = new Thread(() -> {
            try {
                while (currSeconds > 0) {
                    if (!isPaused) {        // 일시정지/ 재개 버튼 눌렸으면
                        Platform.runLater(this::setOutput);
                        currSeconds--; // 먼저 감소
                        temp++;
                        if (currSeconds < 6) {
//                            // 6보다 작다면 5초전에 삐비빅 해서 알람
                            java.awt.Toolkit.getDefaultToolkit().beep();
                        }
                    }
                    Thread.sleep(1000); // 그 후에 대기
                }
                Platform.runLater(() -> { // 0에 도달하면
                    updateTotalStudyTimeLabel(); // 흐른 시간 업데이트
                    temp = 0; // 임시 시간 초기화
                    isTimerRunningStatic = false;             // false 로 바꿈 이제 타이머 안하고있따.
                    scrollDown(); // 다운 스크롤
                });
            } catch (InterruptedException e) {
                System.out.println("thread out" + e);
                Thread.currentThread().interrupt(); // 스레드 인터럽트 상태를 설정
            }
        });
        thrd.start();
    }


    void updateTotalStudyTimeLabel() {

        total += temp;

        // 여기를 이제 시분초로 나오게
        LinkedList<Integer> totalStudyhms = secondsToHms(total);
        totalStudyTime.setText((numberMap.get(totalStudyhms.get(0))) + "시"
                             + (numberMap.get(totalStudyhms.get(1))) + "분"
                             + (numberMap.get(totalStudyhms.get(2))) + "초");


        LocalDateTime endTime = LocalDateTime.now(); // 종료 시간 기록

        // 타이머가 종료되면 음악 재생 중지
        if (mediaPlayer != null) {  // null 이 아니라면
            mediaPlayer = Modalcontroller.shutDown(mediaPlayer); // 아예 없앰. mediaPlayer 리소스 없애게하려고 이거되나??
        }

        logEntries.add(new TimerLogEntry(currentStartTime, endTime, temp)); // 로그엔트리에 저장



        dbConnector.insertLogEntryAndUpdateTotalTime(new TimerLogEntry(currentStartTime, endTime, temp)); // DB에 로그 삽입
        loadLogEntriesForCurrentDate(); // 현재 날짜의 로그를 다시 로드하여 ListView 업데이트

//        selectTotalTime(LocalDate.now());   // 현재 날짜에 대한 총공부시간 갖고와서
        // 여기에 업ㄷ데이트

        // DB에 로그 삽입 및 총 공부 시간 업데이트
//        DBConnector.insertLogEntryAndUpdateTotalTime(new TimerLogEntry(currentStartTime, endTime, temp));
        updateTotalStudyTimeLabelFromDB(); // 총 공부 시간을 다시 로드하고 라벨을 업데이트합니다.
    }



    // 이건 왜?
    void setOutput() {
        LinkedList<Integer> currHms = secondsToHms(currSeconds);    // 현재입력받은 것인데 linkedList 로 시분초 012 ㅇㅇ
        System.out.println(currHms.get(0) + "-" + currHms.get(1) + " - " + currHms.get(2)); // 시간 잘찍히는지 확인
        hoursTimer.setText(numberMap.get(currHms.get(0)));      // 시 분 초 업데이트
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

            // 취소 버튼을 눌렀을 때 음악 재생 중지
            if (mediaPlayer != null) {
                mediaPlayer = Modalcontroller.shutDown(mediaPlayer);
            }

            scrollDown(); // 스크롤 다운 메서드를 호출합니다.
        }
    }

    @FXML
    private void toggleTime(ActionEvent event) {
        isPaused = !isPaused; // 일시정지 상태를 전환
        // 퍼즈 상태를 트루 펄스 바꿔줌
        if (isPaused) {   // true 면 멈춤
            mediaPlayer.stop();
        }
        if (!isPaused) {   // true 면 멈춤
            mediaPlayer.play();
        }
    }


    Integer hmsToSeconds(Integer h, Integer m, Integer s) {
        Integer hToSeconds = h * 3600;
        Integer mToSecodns = m * 60;
        Integer total = hToSeconds + mToSecodns + s;
        return total;
    }
    // 전부 초로 바꿔준다.

    LinkedList<Integer> secondsToHms(Integer currSeconds) {
        Integer hours = currSeconds / 3600;
        currSeconds = currSeconds % 3600;
        Integer minutes = currSeconds / 60;
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

        TranslateTransition tr1 = new TranslateTransition();
        tr1.setDuration(Duration.millis(100));
        tr1.setToX(0);
        tr1.setToY(-3000);  // 그냥화면밖으로 날려보냄
        tr1.setToX(-1000);
        tr1.setToY(0);
        tr1.setNode(menuPane);


        TranslateTransition tr2 = new TranslateTransition();
        tr2.setDuration(Duration.millis(100));
        tr2.setFromX(0);
        tr2.setFromY(200);
        tr2.setToX(0);
        tr2.setToY(0);
        tr2.setNode(timerPane);
        ParallelTransition pt = new ParallelTransition(tr1, tr2);

        pt.play();
    }


    void scrollDown() {
        // 트랜스레이션 생성
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
        ObservableList<Integer> hoursList = FXCollections.observableArrayList();    // 이거 왜 해주는거지?
        ObservableList<Integer> minutesAndSecondsList = FXCollections.observableArrayList();

        for (int i = 0; i <= 60; i++) {
            if (0 <= i && i <= 24) {
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
        for (Integer i = 0; i <= 60; i++) {
            if (0 <= i && i <= 9) {
                numberMap.put(i, "0" + i.toString());
            } else {
                numberMap.put(i, i.toString());
            }
        }
        // tree Map? <왜 문자열이들어가지?>

        loadLogEntriesForCurrentDate(); // 현재 날짜에 해당하는 로그 엔트리 로드 및 표시

        // 현재 날짜에 대한 총 공부 시간 로드 및 라벨 업데이트
        updateTotalStudyTimeLabelFromDB();

        // 타이머가 실행 중이면 타이머 화면을 보여준다
        if (isTimerRunningStatic) {
            scrollUp();
        }

        // 되긴하는데,,



    }


    private void updateTotalStudyTimeLabelFromDB() {
        int totalTime = dbConnector.selectTotalTime(LocalDate.now());
        totalStudyTime.setText(formatDuration(totalTime)); // 'formatDuration' 메소드는 이미 정의되어 있다고 가정


    }



    private void loadLogEntriesForCurrentDate() {
        LocalDate currentDate = LocalDate.now(); // 현재 날짜 가져오기
        List<TimerLogEntry> entries = dbConnector.selectLogEntriesByDate(currentDate); // 해당 날짜의 로그 엔트리 가져오기

        // ListView 업데이트
        ObservableList<String> logList = FXCollections.observableArrayList();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (TimerLogEntry entry : entries) {
            String startTime = entry.getStartTime().format(formatter);
            String endTime = entry.getEndTime().format(formatter);
            String duration = formatDuration(entry.getDurationInSeconds());
            String logText = String.format("시작: %s, 종료: %s, 지속: %s", startTime, endTime, duration);
            logList.add(logText);
        }

        timerLogView.setItems(logList);
    }

    private String formatDuration(int totalSeconds) {
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;
        return String.format("%d시간 %d분 %d초", hours, minutes, seconds);
    }



}