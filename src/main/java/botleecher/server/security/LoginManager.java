package botleecher.server.security;

public interface LoginManager {

    boolean isLoginValid(final String login, final String password) throws Exception;

    void addLogin(final String login, final String password) throws Exception;

    int countUsers() throws Exception;
}
