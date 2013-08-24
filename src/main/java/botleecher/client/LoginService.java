package botleecher.client;

import botleecher.client.domain.SessionClient;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Maxime Guennec
 * Date: 11/08/13
 * Time: 04:21
 * To change this template use File | Settings | File Templates.
 */
@RemoteServiceRelativePath("LoginService")
public interface LoginService extends RemoteService {

    String login(final String login, final String password) throws Exception;

    void logout(final String sessionId) throws Exception;

    boolean isSessionValid(final String login, final String sessionId) throws Exception;

    void addAccount(final String login, final String password) throws Exception;

    void deleteAccount(final SessionClient session, final String login) throws Exception;

    List<String> getAllAccounts(final SessionClient session) throws Exception;

    public static class App {
        private static LoginServiceAsync ourInstance = GWT.create(LoginService.class);

        public static synchronized LoginServiceAsync getInstance() {
            return ourInstance;
        }
    }
}
