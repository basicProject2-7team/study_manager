package com.sm.study_manager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import java.sql.Connection;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        // 데이터베이스 연결 시도
        try (Connection connection = DBConnector.getConnection()) {
            System.out.println("Database connected!");
            // 연결을 사용한 작업 수행
        } catch (Exception e) {
            System.out.println("Database connection failed.");
            e.printStackTrace();
            // 연결 실패 처리
        }

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("CalenderView.fxml"));


        Scene scene = new Scene(fxmlLoader.load(), 600, 800);
        stage.setTitle("Study-Manager");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
