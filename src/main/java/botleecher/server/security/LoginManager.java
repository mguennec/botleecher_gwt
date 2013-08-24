package botleecher.server.security;

import org.jdom2.JDOMException;

import java.io.IOException;
import java.util.List;

public interface LoginManager {

    boolean isLoginValid(final String login, final String password) throws Exception;

    void addLogin(final String login, final String password) throws Exception;

    void deleteLogin(final String login) throws JDOMException, IOException;

    int countUsers() throws Exception;

    List<String> getAllUsers() throws JDOMException, IOException;
}
