package com.sm.study_manager;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;

public class DatabaseConnection {
    public static void connectToDatabase() {
        MongoClient mongoClient = MongoClients.create("mongodb+srv://qwer:qwer@studytimer.jnnzfby.mongodb.net/");
        MongoDatabase database = mongoClient.getDatabase("myDatabase");
        // 여기서 데이터베이스 작업을 수행합니다.
    }
}