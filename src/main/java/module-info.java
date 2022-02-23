module com.example.chat {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires sqlite.jdbc;
    requires org.apache.logging.log4j;

    opens JustChat to javafx.fxml;
    exports JustChat;
}