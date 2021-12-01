package JustChat;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class ChatApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ChatApp.class.getResource("/chat-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 420, 540);
        stage.setTitle("");
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(new EventHandler<WindowEvent>(){
            @Override
            public void handle(WindowEvent windowEvent) {

            }
        });
    }

    public static void main(String[] args) {
        launch();
    }
}