package JustChat;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class ChatController {
    @FXML
    TextArea mainTextArea;
    @FXML
    TextField mainTextField;

    @FXML
    protected void onButtonClick() {
        if(!mainTextField.getText().isEmpty()){
            mainTextArea.appendText("-" + mainTextField.getText().trim() + "\n");
            mainTextField.clear();
            }
        }
    }


