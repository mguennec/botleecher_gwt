package botleecher.client.component;

import botleecher.client.AsyncCallbackAdapter;
import botleecher.client.BotLeecherGwt;
import botleecher.client.MediatorService;
import botleecher.client.MediatorServiceAsync;
import botleecher.client.event.PackListEvent;
import botleecher.client.event.UserListEvent;
import botleecher.client.listener.BotLeecherAdapter;
import botleecher.shared.KeyProvider;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.*;
import com.sencha.gxt.core.client.ToStringValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.StringLabelProvider;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.NorthSouthContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.form.SimpleComboBox;
import de.novanic.eventservice.client.event.RemoteEventService;
import de.novanic.eventservice.client.event.RemoteEventServiceFactory;
import de.novanic.eventservice.client.event.domain.DomainFactory;

import java.util.List;


public class BotPanel extends Composite {

    private SimpleComboBox<String> server = new SimpleComboBox<String>(new StringLabelProvider<String>());
    private SimpleComboBox<String> channel = new SimpleComboBox<String>(new StringLabelProvider<String>());
    private TextButton connect = new TextButton("Connect");
    private ListStore<String> personsStore = new ListStore<String>(new KeyProvider());
    private ListView<String, String> persons;
    private BotTabPanel tabs = new BotTabPanel();

    private MediatorServiceAsync mediatorService = MediatorService.App.getInstance();

    public BotPanel() throws Exception {
        persons = new ListView<String, String>(personsStore, new ToStringValueProvider<String>()) {
            @Override
            public void onBrowserEvent(Event event) {
                super.onBrowserEvent(event);
                if (event.getTypeInt() == Event.ONDBLCLICK) {
                    mediatorService.getList(BotLeecherGwt.getSession(), persons.getSelectionModel().getSelectedItem(), new AsyncCallbackAdapter<Void>("mediatorService.getList"));
                }
            }
        };
        persons.setHeight(80);
        persons.setAllowTextSelection(false);
        //persons.getElement().setClassName("no_select", true);
        remoteEventHandle();
        addClickHandlers();
        final VerticalPanel completePanel = new VerticalPanel();
        completePanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        completePanel.setWidth("95%");
        final HorizontalPanel border = new HorizontalPanel();
        border.setBorderWidth(1);
        final HorizontalPanel panel = new HorizontalPanel();
        border.add(panel);
        panel.setSpacing(2);
        final Grid grid = new Grid(3, 2);
        grid.setWidget(0, 0, new Label("Server"));
        grid.setWidget(1, 0, new Label("Channel"));
        grid.setWidget(0, 1, server);
        grid.setWidget(1, 1, channel);
        grid.setWidget(2, 1, connect);
        panel.add(grid);
        channel.focus();

        panel.add(persons);
        persons.hide();
        completePanel.add(border);
        completePanel.add(tabs);
        initData();
        final NorthSouthContainer comp = new NorthSouthContainer();
        comp.setNorthWidget(getMenu());
        comp.setSouthWidget(completePanel);
        initWidget(comp);
    }

    private Widget getMenu() {
        return new BotMenu();
    }

    private void remoteEventHandle() {
        final RemoteEventServiceFactory factory = RemoteEventServiceFactory.getInstance();
        final RemoteEventService service = factory.getRemoteEventService();
        service.addListener(DomainFactory.getDomain("bot"), new BotLeecherAdapter() {
            public void onUserListEvent(UserListEvent event) {
                setUserList(event.getUsers());
            }
            public void onPackListEvent(PackListEvent event) {
                tabs.addBot(event.getNick(), event.getPacks());
            }
        });
    }

    private void addClickHandlers() {
        connect.addSelectHandler(new SelectEvent.SelectHandler() {
            @Override
            public void onSelect(SelectEvent selectEvent) {
                tabs.clearBotTabs();
                mediatorService.connect(BotLeecherGwt.getSession(), server.getCurrentValue(), channel.getCurrentValue(), new AsyncCallbackAdapter<Void>());
            }
        });
    }

    private void initData() throws Exception {
        mediatorService.getServers(BotLeecherGwt.getSession(), new AsyncCallbackAdapter<List<String>>("Mediator.getServers") {
            @Override
            public void onSuccess(List<String> result) {
                if (result != null && !result.isEmpty()) {
                    server.add(result);
                    server.setValue(result.get(0));
                }
            }
        });
        mediatorService.getChannels(BotLeecherGwt.getSession(), new AsyncCallbackAdapter<List<String>>("Mediator.getChannels") {
            @Override
            public void onSuccess(List<String> result) {
                if (result != null && !result.isEmpty()) {
                    channel.add(result);
                    channel.setValue(result.get(0));
                }
            }
        });
        mediatorService.getUsers(BotLeecherGwt.getSession(), new AsyncCallbackAdapter<List<String>>("Mediator.getUsers") {
            @Override
            public void onSuccess(List<String> result) {
                if (result != null && !result.isEmpty()) {
                    setUserList(result);
                }
            }
        });
    }

    private void setUserList(List<String> result) {
        personsStore.clear();
        personsStore.addAll(result);
        persons.show();
    }

}
