package botleecher.client.component;

import botleecher.client.BotLeecherGwt;
import botleecher.client.LoginService;
import botleecher.client.LoginServiceAsync;
import botleecher.client.domain.SessionClient;
import botleecher.server.security.SessionManager;
import botleecher.shared.EnterChangeFocusKeyUpHandler;
import botleecher.shared.EnterValidateKeyUpHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Maxime Guennec
 * Date: 23/08/13
 * Time: 13:10
 * To change this template use File | Settings | File Templates.
 */
public class LoginBox extends DialogBox {
    private static LoginServiceAsync loginService = LoginService.App.getInstance();

    public LoginBox() {
        super();
        setText("Login Verification");
        final VerticalPanel verticalPanel = new VerticalPanel();
        add(verticalPanel);
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
                                BotLeecherGwt.setSession(new SessionClient(login, result));
                                Date expires = new Date(System.currentTimeMillis() + SessionManager.SESSION_DURATION);
                                Cookies.setCookie(BotLeecherGwt.SID_COOKIE_NAME, result, expires, null, "/", false);
                                Cookies.setCookie(BotLeecherGwt.USER_COOKIE_NAME, login, expires, null, "/", false);
                                BotLeecherGwt.showPanel();
                            }
                        }
                    });
                } catch (Exception e) {
                    error.setText(e.getMessage());
                }
            }
        });
        loginBox.addKeyUpHandler(new EnterChangeFocusKeyUpHandler(passwordBox));
        passwordBox.addKeyUpHandler(new EnterValidateKeyUpHandler(loginButton));
        verticalPanel.add(loginButton);
    }
}
