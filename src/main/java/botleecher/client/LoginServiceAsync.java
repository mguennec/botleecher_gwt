package botleecher.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Created with IntelliJ IDEA.
 * User: Maxime Guennec
 * Date: 11/08/13
 * Time: 04:21
 * To change this template use File | Settings | File Templates.
 */
public interface LoginServiceAsync {

    void login(final String login, final String password, AsyncCallback<String> async);

    void logout(final String sessionId, AsyncCallback<Void> async);

    void isSessionValid(final String login, final String sessionId, AsyncCallback<Boolean> async);

    void addAccount(final String login, final String password, AsyncCallback<Void> async);

}
