import java.sql.SQLException;

public interface AuthProvider {
    String getUsernameBuLoginAndPassword(String login, String password);
     void connect();
     void disConnect();

}
