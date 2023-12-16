package com.sm.study_manager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class SharedData {
    private static final ObservableList<String> sharedMusicList = FXCollections.observableArrayList();

    public static ObservableList<String> getSharedMusicList() {
        return sharedMusicList;
    }
}