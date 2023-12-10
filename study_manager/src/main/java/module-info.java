module com.sm.study_manager {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires org.mongodb.driver.sync.client;

    opens com.sm.study_manager to javafx.fxml;
    exports com.sm.study_manager;
}