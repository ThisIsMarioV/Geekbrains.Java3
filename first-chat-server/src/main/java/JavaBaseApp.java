import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class JavaBaseApp {

    private static Connection connection;
    private static Statement statement;
    public static void main(String[] args) {
        try {
            connect();
            createTable();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            disConnect();
        }

    }
    public static void connect() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:javadb.db");
        statement = connection.createStatement();

    }
    public static void disConnect()  {
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
    public static void createTable() throws SQLException {
        String sql = "create table if not exists student (\n" +
                "id integer primary key autoincrement not null,\n" +
                "name text not null,\n" +
                "score integer not null\n" +
                ");";
        System.out.println(sql);
        statement.executeUpdate(sql);
    }
}
