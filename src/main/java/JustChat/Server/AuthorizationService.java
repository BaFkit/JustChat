package JustChat.Server;

public interface AuthorizationService {
    void start();
    String getNickByLoginPass(String login, String pass);
    void stop();
}
