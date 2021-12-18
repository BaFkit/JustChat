package JustChat.Server;

import java.sql.SQLException;

public interface AuthorizationService {
    void start() throws ClassNotFoundException, SQLException;
    String getNickByLoginPass(String login, String pass);
    boolean changeNick(String nickIn, String nickTo);
    void stop();
}
