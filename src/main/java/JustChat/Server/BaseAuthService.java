package JustChat.Server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class BaseAuthService implements AuthorizationService {

    private final List<Entry> entries;
    Connection connection;
    PreparedStatement ps;
    ResultSet rs;

    public BaseAuthService(){
    entries = new ArrayList<>();
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
    public void start() throws ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:users.db");
            ps = connection.prepareStatement("SELECT * FROM users");
            rs = ps.executeQuery();
            while (rs.next()) {
                entries.add(new Entry(rs.getString("login"), rs.getString("pass"), rs.getString("nick")));
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Сервис аутентификации запущен");
    }
    @Override
    public void stop() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Сервис аутентификации остановлен");
    }

    public boolean changeNickInBase(String nickIn, String nickTo) throws SQLException {

        ps = connection.prepareStatement("UPDATE users SET nick = ? WHERE nick = ?");
        ps.setString(1, nickTo);
        ps.setString(2, nickIn);
        int result;
        result = ps.executeUpdate();
        return result != 0;
    }

    private class Entry {
        private final String login;
        private final String pass;
        private final String nick;

        public Entry(String login, String pass, String nick) {
            this.login = login;
            this.pass = pass;
            this.nick = nick;
        }

        @Override
        public String toString() {
            return "Entry{" +
                    "login='" + login + '\'' +
                    ", pass='" + pass + '\'' +
                    ", nick='" + nick + '\'' +
                    '}';
        }
    }
}


