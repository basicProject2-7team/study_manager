module com.sm.study_manager {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;
    requires javafx.media;
    // 메디아
    opens com.sm.study_manager to javafx.fxml;
    exports com.sm.study_manager;
}