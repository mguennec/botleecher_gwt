package botleecher.client.component;

import botleecher.client.*;
import botleecher.shared.EnterChangeFocusKeyUpHandler;
import botleecher.shared.EnterValidateKeyUpHandler;
import botleecher.shared.KeyProvider;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.sencha.gxt.core.client.ToStringValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.menu.*;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Maxime Guennec
 * Date: 17/08/13
 * Time: 00:52
 * To change this template use File | Settings | File Templates.
 */
public class BotMenu extends MenuBar {

    private TextButton nicks = new TextButton("OK");
    private TextButton path = new TextButton("OK");
    private TextButton keywords = new TextButton("OK");

    private MediatorServiceAsync mediatorService = MediatorService.App.getInstance();

    private LoginServiceAsync loginService = LoginService.App.getInstance();

    private final ListStore<String> usersStore = new ListStore<String>(new KeyProvider());
    private ListView<String, String> users;

    public BotMenu() {
        super();
        users = new ListView<String, String>(usersStore, new ToStringValueProvider<String>()) {
            @Override
            public void onBrowserEvent(Event event) {
                super.onBrowserEvent(event);
                if (event.getTypeInt() == Event.ONDBLCLICK) {
                    final Dialog dialog = new Dialog();
                    final String user = users.getSelectionModel().getSelectedItem();
                    dialog.setHideOnButtonClick(true);
                    dialog.add(new Label("Do you want to delete " + user + " ?"));
                    dialog.setPredefinedButtons(Dialog.PredefinedButton.YES, Dialog.PredefinedButton.NO);
                    dialog.addHideHandler(new HideEvent.HideHandler() {
                        @Override
                        public void onHide(HideEvent event) {
                            if (dialog.getHideButton() == dialog.getButtonById(Dialog.PredefinedButton.YES.name())) {
                                loginService.deleteAccount(BotLeecherGwt.getSession(), user, new AsyncCallbackAdapter<Void>("DeleteAccount") {
                                    /**
                                     * Called when an asynchronous call completes successfully.
                                     *
                                     * @param result the return value of the remote produced call
                                     */
                                    @Override
                                    public void onSuccess(Void result) {
                                        loadUsers();
                                        if (user.equalsIgnoreCase(BotLeecherGwt.getSession().getUser())) {
                                            Window.Location.reload();
                                        }
                                    }
                                });
                            }
                        }
                    });
                    dialog.show();
                }
            }
        };

        final Dialog settingsBox = new Dialog();
        settingsBox.setClosable(true);
        settingsBox.setPredefinedButtons();
        settingsBox.setTitle("Settings");
        final Grid panel = new Grid(1, 2);
        final Label label = new Label();
        panel.setWidget(0, 0, label);
        final TextBox valueHolder = new TextBox();
        panel.setWidget(0, 1, valueHolder);
        settingsBox.add(panel);
        setId("menu");
        final Menu settingsMenu = new Menu();
        add(new MenuBarItem("Settings", settingsMenu));
        final MenuItem pathItem = new MenuItem("Path");
        settingsMenu.add(pathItem);
        pathItem.addSelectionHandler(getPathHandler(settingsBox, label, valueHolder));

        final MenuItem nicksItem = new MenuItem("Nicks");
        settingsMenu.add(nicksItem);
        nicksItem.addSelectionHandler(getNicksHandler(settingsBox, label, valueHolder));
        final MenuItem keywordsItem = new MenuItem("Keywords");
        settingsMenu.add(keywordsItem);
        keywordsItem.addSelectionHandler(getKeywordsHandler(settingsBox, label, valueHolder));
        final Menu session = new Menu();
        add(new MenuBarItem("Session", session));
        final MenuItem disconnect = new MenuItem("Logout");
        session.add(disconnect);
        disconnect.addSelectionHandler(getDisconnectHandler());
        final Menu admin = new Menu();
        add(new MenuBarItem("Admin", admin));
        final MenuItem addAccount = new MenuItem("Add account");
        admin.add(addAccount);
        addAccount.addSelectionHandler(getAddAccountHandler());
        final MenuItem deleteAccount = new MenuItem("Delete account");
        admin.add(deleteAccount);
        deleteAccount.addSelectionHandler(getDeleteAccountHandler());
        addSettingsHandlers(settingsBox, valueHolder);
    }

    private void loadUsers() {
        loginService.getAllAccounts(BotLeecherGwt.getSession(), new AsyncCallbackAdapter<List<String>>() {
            /**
             * Called when an asynchronous call completes successfully.
             *
             * @param result the return value of the remote produced call
             */
            @Override
            public void onSuccess(List<String> result) {
                usersStore.clear();
                usersStore.addAll(result);
            }
        });
    }

    private SelectionHandler<Item> getPathHandler(final Dialog settingsBox, final Label label, final TextBox valueHolder) {
        return new SelectionHandler<Item>() {
            @Override
            public void onSelection(SelectionEvent<Item> event) {
                label.setText("Path");
                mediatorService.getSavePath(BotLeecherGwt.getSession(), new AsyncCallbackAdapter<String>("Mediator.getSavePath") {
                    @Override
                    public void onSuccess(String result) {
                        super.onSuccess(result);
                        valueHolder.setText(result);
                        settingsBox.getButtonBar().remove(nicks);
                        settingsBox.getButtonBar().remove(keywords);
                        settingsBox.addButton(path);
                        settingsBox.show();
                    }
                });
            }
        };
    }

    private SelectionHandler<Item> getNicksHandler(final Dialog settingsBox, final Label label, final TextBox valueHolder) {
        return new SelectionHandler<Item>() {
            @Override
            public void onSelection(SelectionEvent<Item> event) {
                label.setText("Nicks");
                mediatorService.getNicks(BotLeecherGwt.getSession(), new AsyncCallbackAdapter<String>("Mediator.getNicks") {
                    @Override
                    public void onSuccess(String result) {
                        super.onSuccess(result);
                        valueHolder.setText(result);
                        settingsBox.getButtonBar().remove(path);
                        settingsBox.getButtonBar().remove(keywords);
                        settingsBox.addButton(nicks);
                        settingsBox.show();
                    }
                });
            }
        };
    }

    private SelectionHandler<Item> getKeywordsHandler(final Dialog settingsBox, final Label label, final TextBox valueHolder) {
        return new SelectionHandler<Item>() {
            @Override
            public void onSelection(SelectionEvent<Item> event) {
                label.setText("Keywords");
                mediatorService.getKeywords(BotLeecherGwt.getSession(), new AsyncCallbackAdapter<String>("Mediator.getKeywords") {
                    @Override
                    public void onSuccess(String result) {
                        super.onSuccess(result);
                        valueHolder.setText(result);
                        settingsBox.getButtonBar().remove(path);
                        settingsBox.getButtonBar().remove(nicks);
                        settingsBox.addButton(keywords);
                        settingsBox.show();
                    }
                });
            }
        };
    }

    private SelectionHandler<Item> getAddAccountHandler() {
        return new SelectionHandler<Item>() {
            @Override
            public void onSelection(SelectionEvent<Item> event) {
                final Dialog addAccountDialog = new Dialog();
                addAccountDialog.setHideOnButtonClick(true);
                final Label loginLabel = new Label("Login");
                final Label passLabel = new Label("Password");
                final TextBox loginBox = new TextBox();
                final TextBox passBox = new PasswordTextBox();
                final Grid grid = new Grid(2, 2);
                grid.setWidget(0, 0, loginLabel);
                grid.setWidget(1, 0, passLabel);
                grid.setWidget(0, 1, loginBox);
                grid.setWidget(1, 1, passBox);
                loginBox.addKeyUpHandler(new EnterChangeFocusKeyUpHandler(passBox));
                passBox.addKeyUpHandler(new EnterValidateKeyUpHandler(addAccountDialog.getButtonById(Dialog.PredefinedButton.OK.name())));
                addAccountDialog.add(grid);
                addAccountDialog.addHideHandler(new HideEvent.HideHandler() {
                    @Override
                    public void onHide(HideEvent event) {
                        if (addAccountDialog.getHideButton() == addAccountDialog.getButtonById(Dialog.PredefinedButton.OK.name())) {
                            loginService.addAccount(loginBox.getText(), passBox.getText(), new AsyncCallbackAdapter<Void>("LoginService.addAccount"));
                        }
                    }
                });
                addAccountDialog.show();
                loginBox.setFocus(true);
            }
        };
    }
    private SelectionHandler<Item> getDeleteAccountHandler() {
        return new SelectionHandler<Item>() {
            @Override
            public void onSelection(SelectionEvent<Item> event) {
                loadUsers();
                users.setHeight(80);
                final Dialog deleteAccountDialog = new Dialog();
                deleteAccountDialog.setHideOnButtonClick(true);
                deleteAccountDialog.setPredefinedButtons(Dialog.PredefinedButton.CLOSE);
                deleteAccountDialog.add(users);
                deleteAccountDialog.show();
            }
        };
    }

    private SelectionHandler<Item> getDisconnectHandler() {
        return new SelectionHandler<Item>() {
            @Override
            public void onSelection(SelectionEvent<Item> event) {
                if (BotLeecherGwt.getSession() != null) {
                    final AsyncCallbackAdapter<Void> logout = new AsyncCallbackAdapter<Void>("Logout") {
                        @Override
                        public void onSuccess(Void result) {
                            Window.Location.reload();
                        }
                    };
                    loginService.logout(BotLeecherGwt.getSession().getUuid(), logout);
                }
            }
        };
    }

    private void addSettingsHandlers(final Dialog settingsBox, final TextBox valueHolder) {
        path.addSelectHandler(new SelectEvent.SelectHandler() {
            @Override
            public void onSelect(SelectEvent selectEvent) {
                mediatorService.setSavePath(BotLeecherGwt.getSession(), valueHolder.getText(), new AsyncCallbackAdapter<Void>("Mediator.setSavePath"));
                settingsBox.hide();
            }
        });
        nicks.addSelectHandler(new SelectEvent.SelectHandler() {
            @Override
            public void onSelect(SelectEvent selectEvent) {
                mediatorService.setNicks(BotLeecherGwt.getSession(), valueHolder.getText(), new AsyncCallbackAdapter<Void>("Mediator.setNicks"));
                settingsBox.hide();
            }
        });
        keywords.addSelectHandler(new SelectEvent.SelectHandler() {
            @Override
            public void onSelect(SelectEvent selectEvent) {
                mediatorService.setKeywords(BotLeecherGwt.getSession(), valueHolder.getText(), new AsyncCallbackAdapter<Void>("Mediator.setKeywords"));
                settingsBox.hide();
            }
        });
    }
}
