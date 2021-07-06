import java.sql.SQLException;

public interface AuthProvider {
    String getUsernameBuLoginAndPassword(String login, String password) throws SQLException;
     void connect() throws SQLException;
     void disConnect() throws SQLException;

}
