package JustChat.Server;

import java.util.ArrayList;
import java.util.List;

public class BaseAuthService implements AuthorizationService {

    private List<Entry> entries;

    public BaseAuthService() {
        entries = new ArrayList<>();
        entries.add(new Entry("login1", "pass1", "nick1"));
        entries.add(new Entry("login2", "pass2", "nick2"));
        entries.add(new Entry("login3", "pass3", "nick3"));
        entries.add(new Entry("login4", "pass4", "nick4"));
    }






    @Override
    public String getNickByLoginPass(String login, String pass) {
        for (Entry a: entries){
            if (a.login.equals(login) && a.pass.equals(pass)){
                return a.nick;
            }
        }
        return null;
    }
    @Override
    public void start() {
        System.out.println("Сервис аутентификации запущен");
    }
    @Override
    public void stop() {
        System.out.println("Сервис аутентификации остановлен");
    }





    private class Entry {
        private String login;
        private String pass;
        private String nick;

        public Entry(String login, String pass, String nick) {
            this.login = login;
            this.pass = pass;
            this.nick = nick;
        }
    }
}


