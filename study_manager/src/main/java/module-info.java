module com.sm.study_manager {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;
    requires javafx.media;
    requires java.sql;
    requires com.zaxxer.hikari;
    //자바 sql 임폴트
    // 메디아 임폴트?
    opens com.sm.study_manager to javafx.fxml;
    exports com.sm.study_manager;
}

