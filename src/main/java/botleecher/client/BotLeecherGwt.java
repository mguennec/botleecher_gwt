package botleecher.client;

import botleecher.client.component.BotPanel;
import botleecher.client.component.LoginBox;
import botleecher.client.domain.SessionClient;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class BotLeecherGwt implements EntryPoint {
    public static final String SID_COOKIE_NAME = "sid";
    public static final String USER_COOKIE_NAME = "user";
    /**
     * The message displayed to the user when the server cannot be reached or
     * returns an error.
     */
    private static final String SERVER_ERROR = "An error occurred while "
            + "attempting to contact the server. Please check your network "
            + "connection and try again.";
    private static SessionClient session;
    private static BotPanel botPanel;
    private static DialogBox dialogBox;
    private final Messages messages = GWT.create(Messages.class);
    private static LoginServiceAsync loginService = LoginService.App.getInstance();

    public static void setSession(SessionClient session) {
        BotLeecherGwt.session = session;
    }

    public static SessionClient getSession() {
        return session;
    }

    public static void sessionExpired() {
        showLoginBox();
    }

    public static void showLoginBox() {
        if (dialogBox == null) {
            createDialogBox();
        }
        if (botPanel != null) {
            botPanel.setVisible(false);
        }
        if (dialogBox != null) {
            dialogBox.center();
            dialogBox.show();
        }

    }

    public static void showPanel() {
        if (botPanel == null) {
            createApplication();
        }
        if (botPanel != null) {
            botPanel.setVisible(true);
        }
        if (dialogBox != null) {
            dialogBox.hide();
        }

    }

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
        final AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {
            @Override
            public void onFailure(Throwable caught) {
                showLoginBox();
            }

            @Override
            public void onSuccess(Boolean result) {
                if (result) {
                    showPanel();
                } else {
                    showLoginBox();
                }
            }
        };
        if (session == null) {
            final String sid = Cookies.getCookie(SID_COOKIE_NAME);
            final String user = Cookies.getCookie(USER_COOKIE_NAME);
            if (sid != null && user != null) {
                session = new SessionClient(user, sid);
                loginService.isSessionValid(user, sid, callback);
            } else {
                showLoginBox();
            }
        } else {
            loginService.isSessionValid(session.getUser(), session.getUuid(), callback);
        }
    }

    private static void createDialogBox() {
        dialogBox = new LoginBox();
        RootPanel.get().add(dialogBox);
        dialogBox.hide();
    }

    private static void createApplication() {
        if (botPanel == null) {
            botPanel = new BotPanel();
            RootPanel.get().add(botPanel);
            botPanel.setVisible(false);
        }
    }
}
