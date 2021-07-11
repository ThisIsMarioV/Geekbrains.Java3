import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JavaBaseAuthProvider implements AuthProvider {
    private static Connection connection;
    private static Statement statement;

    public static void createTable() throws SQLException {
        String sql = "create table if not exists user (\n" +
             "id integer primary key autoincrement not null,\n" +
                "login text not null,\n" +
                "password text not null,\n" +
                "nickname text not null\n" +
                ");";
        statement.executeUpdate(sql);
    }


    public static void insertPs() throws SQLException {
        try (PreparedStatement preparedStatement =
                     connection.prepareStatement("insert into user (login, password, nickname) values (? ,? , ?)")) {
            for (int i = 1; i < 11; i++) {
                preparedStatement.setString(1, "login" + i);
                preparedStatement.setString(2, "password"+ i);
                preparedStatement.setString(3, "nickname"+i);
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        }

    }

    @Override
    public String getUsernameBuLoginAndPassword(String login, String password) throws SQLException {

        insertPs();
        return readTable(login, password);
    }

    @Override
    public void connect() throws SQLException {
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
        createTable();
    }

    @Override
    public void disConnect() throws SQLException {
        dropTable();
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    public static void dropTable() throws SQLException {
        statement.execute("drop table user;");
    }

    public static String readTable(String login, String password) throws SQLException {
        String sql = "select * from user;";
        ResultSet rs = statement.executeQuery(sql);
        while ((rs.next())) {
            if(rs.getString("login").equals(login) && rs.getString("password").equals(password)){
                return rs.getString("nickname");
            }
        }
        return null;
    }

}
