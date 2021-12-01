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
            mainTextArea.appendText(msg);
        }));
    }

    @FXML
    TextArea mainTextArea;
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


}


