package JustChat;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class ChatController implements Initializable {
    private Client client;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        client = new Client((msg -> {
            if (msg.startsWith("/clients")){
                String clients = msg.substring("/clients".length());
                listTextArea.setText(clients.replace(' ', '\n'));

            }else{
                mainTextArea.appendText(msg);
            }
        }));
    }

    @FXML
    TextArea mainTextArea;
    @FXML
    TextArea listTextArea;
    @FXML
    TextField mainTextField;

    @FXML
    protected void onButtonClick() {
        if(!mainTextField.getText().trim().isEmpty()){
            client.sendMessage(mainTextField.getText().trim());
            mainTextField.clear();
            mainTextField.requestFocus();
        }
    }
    @FXML
    protected void send_file() {
        client.sendMessage("/sf");
        client.sendFile();
    }
}
