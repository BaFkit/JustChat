package JustChat.Server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class BaseAuthService implements AuthorizationService {

    private final List<Entry> entries;
    private static Connection connection;
    private static PreparedStatement ps;

    public BaseAuthService(){
        entries = new ArrayList<>();
    }

    private Connection getConnection(){
        try{
            if (connection == null || connection.isClosed()) {
                connect();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public void connect(){
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:users.db");
            ps = connection.prepareStatement("SELECT * FROM users");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                entries.add(new Entry(rs.getInt("id"), rs.getString("login"), rs.getString("pass"), rs.getString("nick")));
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void disconnect(){
        try {
            if (ps != null){
                ps.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
        connect();
        System.out.println("Сервис аутентификации запущен");
    }
    @Override
    public void stop() {
        disconnect();
        System.out.println("Сервис аутентификации остановлен");
    }


    public boolean changeNick(String nickIn, String nickTo) {
        int result = 0;
        try {
            ps = connection.prepareStatement("UPDATE users SET nick = ? WHERE nick = ?");
            ps.setString(1, nickTo);
            ps.setString(2, nickIn);
            result = ps.executeUpdate();
            for (Entry entry: entries){
                if(entry.nick.equals(nickIn)){
                    entry.nick = nickTo;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result != 0;
    }

    private static class  Entry {
        private final int id;
        private final String login;
        private final String pass;
        private String nick;

        public Entry(int id, String login, String pass, String nick) {
            this.id = id;
            this.login = login;
            this.pass = pass;
            this.nick = nick;
        }
    }
}


