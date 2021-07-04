import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JavaBaseAuthProvider implements AuthProvider {
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
                new UserInfo("", "", ""),
                new UserInfo("", "", ""),
                new UserInfo("", "", ""),
                new UserInfo("", "", "")
        ));
    }

    @Override
    public String getUsernameBuLoginAndPassword(String login, String password) {
        for (UserInfo u : userInfoList) {
            if (u.login.equals(login) && u.password.equals(password)) {
                return u.userName;
            }
        }
        return null;
    }
}
