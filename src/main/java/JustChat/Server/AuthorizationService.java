package JustChat.Server;

import java.sql.SQLException;

public interface AuthorizationService {
    void start() throws ClassNotFoundException, SQLException;
    String getNickByLoginPass(String login, String pass);
    boolean changeNickInBase(String nickIn, String nickTo) throws SQLException;
    void stop();
}
