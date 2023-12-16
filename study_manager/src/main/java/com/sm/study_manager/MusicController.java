package com.sm.study_manager;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class MusicController extends CommonController{
//
    @FXML
    private ListView<String> musicFileList; // 음악 파일 리스트 뷰

    @FXML
    private Button insertFileButton; // 파일 추가 버튼



    @FXML
    private void insertFile() {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("음악 파일 선택");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("음악 파일", "*.mp3"));

        // 파일 선택 대화상자를 표시하고 사용자가 선택한 파일을 가져옵니다.
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            try {
                // 사용자 홈 디렉토리 경로를 기반으로 대상 경로를 구성합니다.
                String userHome = System.getProperty("user.home");
                Path dest = Path.of(userHome, "Music", selectedFile.getName());

                // 파일을 지정된 경로로 복사
                Files.copy(selectedFile.toPath(), dest, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("파일이 성공적으로 추가되었습니다: " + dest);
            } catch (Exception e) {
                System.err.println("파일 추가 중 오류 발생: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void initialize(){
        musicFileList.setItems(SharedData.getSharedMusicList());

    }


    // ... 기타 메서드 ...


}
