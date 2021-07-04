import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JavaBaseAuthProvider implements AuthProvider {
    private static Connection connection;
    private static Statement statement;
    private class UserInfo {
        String login;
        String password;
        String userName;
        public UserInfo(String login, String password, String userName) {
            this.login = login;
            this.password = password;
            this.userName = userName;
        }

    }

    private List<UserInfo> userInfoList;



    public void InMemoryAuthProvider() {
        this.userInfoList = new ArrayList<>(Arrays.asList(
                new UserInfo("login1", "password1", "userName1"),
                new UserInfo("login2", "password2", "userName2"),
                new UserInfo("login3", "password3", "userName3"),
                new UserInfo("login4", "password4", "userName4")
        ));
    }
    public static void createTable() throws SQLException {
        String sql = "create table if not exists users (\n" +
                "login integer primary key autoincrement not null,\n" +
                "password text not null,\n" +
                "nickname integer not null\n" +
                ");";
        statement.executeUpdate(sql);
    }
    public static void inserts() throws SQLException {
        String sql = "insert into users (name) values('name" + clientHandler.getUserName() + "');";
        statement.executeUpdate(sql);

    }

    @Override
    public String getUsernameBuLoginAndPassword(String login, String password) {
        InMemoryAuthProvider();
        for (UserInfo u : userInfoList) {
            if (u.login.equals(login) && u.password.equals(password)) {
                return u.userName;
            }
        }
        return null;
    }

    @Override
    public  void connect(){
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:javadb.db");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            statement = connection.createStatement();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    @Override
    public void disConnect() {
        if (statement != null){
            try {
                statement.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        if (connection != null){
            try {
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

    }

}
