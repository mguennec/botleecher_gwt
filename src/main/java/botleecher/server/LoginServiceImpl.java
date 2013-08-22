package botleecher.server;

import botleecher.client.LoginService;
import botleecher.server.security.LoginManager;
import botleecher.server.security.SessionManager;
import com.google.inject.Inject;
import org.jdom2.JDOMException;

import java.io.IOException;


public class LoginServiceImpl extends InjectedRemoteServiceServlet implements LoginService {

    @Inject
    private LoginManager loginManager;

    @Inject
    private SessionManager sessionManager;

    @Override
    public String login(final String login, final String password) throws Exception {
        return loginManager.isLoginValid(login, password) ? sessionManager.createSession(login, getThreadLocalRequest().getRemoteAddr()) : null;
    }

    @Override
    public void logout(String sessionId) throws JDOMException, IOException {
        sessionManager.deleteSession(sessionId);
    }

    @Override
    public boolean isSessionValid(String login, String sessionId) throws Exception {
        return sessionManager.checkSession(login, sessionId, getThreadLocalRequest().getRemoteAddr());
    }

    @Override
    public void addAccount(String login, String password) throws Exception {
        loginManager.addLogin(login, password);
    }
}
