package botleecher.server;

import botleecher.client.LoginService;
import botleecher.client.domain.SessionClient;
import botleecher.server.security.LoginManager;
import botleecher.server.security.SessionManager;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.jdom2.JDOMException;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Singleton
public class LoginServiceImpl extends RemoteServiceServlet implements LoginService {

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

    @Override
    public void deleteAccount(SessionClient session, String login) throws Exception {
        if (session == null || !isSessionValid(session.getUser(), session.getUuid())) {
            throw new IllegalAccessError("You can't delete an account without a valid session");
        }
        loginManager.deleteLogin(login);
        sessionManager.deleteSessionsByUser(login);
    }

    @Override
    public List<String> getAllAccounts(SessionClient session) throws Exception {
        if (session == null || !isSessionValid(session.getUser(), session.getUuid())) {
            throw new IllegalAccessError("You can't delete an account without a valid session");
        }
        final List<String> allUsers = loginManager.getAllUsers();
        Collections.sort(allUsers);
        return allUsers;
    }
}
