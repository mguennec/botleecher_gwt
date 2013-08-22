package botleecher.client;

import botleecher.client.component.BotPanel;
import botleecher.client.domain.SessionClient;
import botleecher.server.security.SessionManager;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.i18n.client.Messages;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;

import java.util.Date;

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
        dialogBox = new DialogBox();
        dialogBox.setText("Login Verification");
        final VerticalPanel verticalPanel = new VerticalPanel();
        dialogBox.add(verticalPanel);
        final Label error = new Label();
        verticalPanel.add(error);
        final TextBox loginBox = new TextBox();
        final PasswordTextBox passwordBox = new PasswordTextBox();
        final Grid grid = new Grid(2, 2);
        grid.setWidget(0, 0, new Label("Login"));
        grid.setWidget(1, 0, new Label("Password"));
        grid.setWidget(0, 1, loginBox);
        grid.setWidget(1, 1, passwordBox);
        verticalPanel.add(grid);

        final TextButton loginButton = new TextButton("Login");
        loginButton.addSelectHandler(new SelectEvent.SelectHandler() {
            @Override
            public void onSelect(SelectEvent selectEvent) {
                try {
                    final String login = loginBox.getText();
                    loginService.login(login, passwordBox.getText(), new AsyncCallback<String>() {

                        @Override
                        public void onFailure(Throwable caught) {
                            error.setText("Error during login.");
                        }

                        @Override
                        public void onSuccess(String result) {
                            if (result == null) {
                                error.setText("Wrong Login/Password.");
                            } else {
                                session = new SessionClient(login, result);
                                Date expires = new Date(System.currentTimeMillis() + SessionManager.SESSION_DURATION);
                                Cookies.setCookie(SID_COOKIE_NAME, result, expires, null, "/", false);
                                Cookies.setCookie(USER_COOKIE_NAME, login, expires, null, "/", false);
                                showPanel();
                            }
                        }
                    });
                } catch (Exception e) {
                    error.setText(e.getMessage());
                }
            }
        });
        final KeyUpHandler keyHandler = new KeyUpHandler() {
                    @Override
                    public void onKeyUp(KeyUpEvent event) {
                        if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                            loginButton.fireEvent(new SelectEvent());
                        }
                    }
        };
        loginBox.addKeyUpHandler(keyHandler);
        passwordBox.addKeyUpHandler(keyHandler);
        verticalPanel.add(loginButton);
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
