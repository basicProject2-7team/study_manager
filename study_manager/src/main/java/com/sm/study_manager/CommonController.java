package com.sm.study_manager;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;


/*
    공통 코드 모음 컨트롤러 입니다.
*/
public class CommonController {

    @FXML
    private Button topCalenderButton;

    @FXML
    private Button topTimerButton;

    @FXML
    private Button topPetButton;

    @FXML
    private Button topMusicButton;

    @FXML
    protected void moveCalender(){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("CalenderView.fxml"));
            Stage stage = (Stage) topCalenderButton.getScene().getWindow();
            stage.setScene(new Scene(fxmlLoader.load()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void moveTimer(){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("TimerView.fxml"));
            Stage stage = (Stage) topTimerButton.getScene().getWindow();
            stage.setScene(new Scene(fxmlLoader.load()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void moveMusic(){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MusicView.fxml"));
            Stage stage = (Stage) topMusicButton.getScene().getWindow();
            stage.setScene(new Scene(fxmlLoader.load()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}