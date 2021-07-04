import java.sql.*;

public class JavaBaseApp {

    private static Connection connection;
    private static Statement statement;
//    public static void main(String[] args) {
//        try {
//            connect();
//            createTable();
//
//            readTable();
//            dropTable();
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }finally {
//            disConnect();
//        }
//
//    }
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
        String sql = "create table if not exists users (\n" +
                "login integer primary key autoincrement not null,\n" +
                "password text not null,\n" +
                "nickname integer not null\n" +
                ");";
        statement.executeUpdate(sql);
    }
//    public static void inserts() throws SQLException {
//        for (int i = 1; i<11; i++){
//            String sql = "insert into students (name, score) values('name" + i + "', " + i % 5 + ");";
//            statement.executeUpdate(sql);
//        }
//    }
public static void inserts(ClientHandler clientHandler) throws SQLException {
            String sql = "insert into students (name) values('name" + clientHandler.getUserName() + "');";
            statement.executeUpdate(sql);

    }
    public static void dropTable() throws SQLException {
        statement.execute("drop table users;");
    }
    static void readTable() throws SQLException {
        String sql = "select * from students;";
        ResultSet rs =statement.executeQuery(sql);
        while ((rs.next())){
            System.out.println(rs.getInt("id") + " " + rs.getString(2)+ " " + rs.getInt("score"));
        }
    }
}
