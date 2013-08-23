package botleecher.client.component;

import botleecher.client.AsyncCallbackAdapter;
import botleecher.client.BotLeecherGwt;
import botleecher.client.MediatorService;
import botleecher.client.MediatorServiceAsync;
import botleecher.client.event.PackListEvent;
import botleecher.client.listener.BotLeecherAdapter;
import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.SingleSelectionModel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import de.novanic.eventservice.client.event.RemoteEventService;
import de.novanic.eventservice.client.event.RemoteEventServiceFactory;
import de.novanic.eventservice.client.event.domain.DomainFactory;

import java.util.Collections;
import java.util.Comparator;


public class BotTab extends Composite implements IFilter<PackListEvent.Pack> {

    private MediatorServiceAsync mediatorService = MediatorService.App.getInstance();

    private final String botName;

    private TextButton refresh = new TextButton("Refresh");
    private TextButton cancel = new TextButton("Cancel");
    private CellTable<PackListEvent.Pack> table = new CellTable<PackListEvent.Pack>();
    private com.sencha.gxt.widget.core.client.ProgressBar progressBar = new com.sencha.gxt.widget.core.client.ProgressBar();
    private TextBox filter = new TextBox();
    private NumberFormat sizeFormat = NumberFormat.getDecimalFormat();

    public BotTab(final String botName) {
        this.botName = botName;
        progressBar.setWidth(500);
        final FilteredListDataProvider<PackListEvent.Pack> data = new FilteredListDataProvider<PackListEvent.Pack>(this);
        final SingleSelectionModel<PackListEvent.Pack> selectionModel = new SingleSelectionModel<PackListEvent.Pack>();
        data.addDataDisplay(table);
        this.table.setSelectionModel(selectionModel);
        table.addDomHandler(new DoubleClickHandler() {
            public void onDoubleClick(DoubleClickEvent doubleClickEvent) {
                mediatorService.getPack(BotLeecherGwt.getSession(), botName, selectionModel.getSelectedObject().getId(), new AsyncCallbackAdapter<Void>("Mediator.getPack"));
            }
        }, DoubleClickEvent.getType());

        final ButtonCell buttonCell = new ButtonCell();
        final Column<PackListEvent.Pack, String> buttonColumn = new Column<PackListEvent.Pack, String>(buttonCell) {
            @Override
            public String getValue(PackListEvent.Pack object) {
                return "Go";
            }
        };
        buttonColumn.setFieldUpdater(new FieldUpdater<PackListEvent.Pack, String>() {
            @Override
            public void update(int index, PackListEvent.Pack object, String value) {
                mediatorService.getPack(BotLeecherGwt.getSession(), botName, object.getId(), new AsyncCallbackAdapter<Void>("Mediator.getPack"));
            }
        });
        table.addColumn(buttonColumn, "");
        final TextColumn<PackListEvent.Pack> nbColumn = new TextColumn<PackListEvent.Pack>() {
            @Override
            public String getValue(PackListEvent.Pack pack) {
                return String.valueOf(pack.getId());
            }
        };
        table.addColumn(nbColumn, "#");
        final TextColumn<PackListEvent.Pack> statusColumn = new TextColumn<PackListEvent.Pack>() {
            @Override
            public String getValue(PackListEvent.Pack pack) {
                return pack.getStatus().toString();
            }
        };
        table.addColumn(statusColumn, "Status");
        final TextColumn<PackListEvent.Pack> nameColumn = new TextColumn<PackListEvent.Pack>() {
            @Override
            public String getValue(PackListEvent.Pack pack) {
                return pack.getName();
            }
        };
        table.addColumn(nameColumn, "Name");
        final TextColumn<PackListEvent.Pack> sizeColumn = new TextColumn<PackListEvent.Pack>() {
            @Override
            public String getValue(PackListEvent.Pack pack) {
                return sizeFormat.format(pack.getSize());
            }
        };
        table.addColumn(sizeColumn, "Size (K)");
        final TextColumn<PackListEvent.Pack> dlColumn = new TextColumn<PackListEvent.Pack>() {
            @Override
            public String getValue(PackListEvent.Pack pack) {
                return String.valueOf(pack.getDownloads());
            }
        };
        table.addColumn(dlColumn, "Dls");

        table.setPageSize(100);
        final Timer t = new Timer() {
            public void run() {
                mediatorService.getFileName(BotLeecherGwt.getSession(), botName, new AsyncCallbackAdapter<String>("Mediator.getFileName") {
                    @Override
                    public void onSuccess(String s) {
                        progressBar.setTitle(s);
                    }
                });
                mediatorService.getProgress(BotLeecherGwt.getSession(), botName, new AsyncCallbackAdapter<Integer>("Mediator.getProgress") {
                    @Override
                    public void onSuccess(Integer integer) {
                        progressBar.updateProgress((double)integer / 100.0, progressBar.getTitle() + " ({0} %)");
                    }
                });
            }
        };

        t.scheduleRepeating(20000);

        final RemoteEventServiceFactory factory = RemoteEventServiceFactory.getInstance();
        final RemoteEventService service = factory.getRemoteEventService();
        service.addListener(DomainFactory.getDomain("bot"), new BotLeecherAdapter() {
            public void onPackListEvent(PackListEvent event) {
                if (event.getNick().equalsIgnoreCase(botName)) {
                    Collections.sort(event.getPacks(), new Comparator<PackListEvent.Pack>() {
                        public int compare(PackListEvent.Pack o1, PackListEvent.Pack o2) {
                            return o2.getId() - o1.getId();
                        }
                    });
                    data.setList(event.getPacks());
                }
            }
        });
        filter.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                data.setFilter(filter.getText());
            }
        });
        init();
    }

    private void init() {
        final SimplePager pager1 = new SimplePager(SimplePager.TextLocation.CENTER, true, true);
        pager1.setDisplay(table);
        pager1.setPageSize(100);
        final SimplePager pager2 = new SimplePager(SimplePager.TextLocation.CENTER, true, true);
        pager2.setDisplay(table);
        pager2.setPageSize(100);
        refresh.addSelectHandler(new SelectEvent.SelectHandler() {
            @Override
            public void onSelect(SelectEvent selectEvent) {
                mediatorService.getList(BotLeecherGwt.getSession(), botName, new AsyncCallbackAdapter<Void>("Mediator.getList"));
            }
        });
        cancel.addSelectHandler(new SelectEvent.SelectHandler() {
            @Override
            public void onSelect(SelectEvent selectEvent) {
                mediatorService.cancel(BotLeecherGwt.getSession(), botName, new AsyncCallbackAdapter<Void>("Mediator.cancel"));
            }
        });
        final VerticalPanel panel = new VerticalPanel();
        final HorizontalPanel buttons = new HorizontalPanel();
        panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        buttons.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        final TextBox pack = new TextBox();
        final TextButton packButton = new TextButton("Get");
        packButton.addSelectHandler(new SelectEvent.SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                final String text = pack.getText();
                if (text != null && !text.trim().isEmpty()) {
                    try {
                        final int packNumber = Integer.valueOf(text);
                        if (packNumber > 1) {
                            mediatorService.getPack(BotLeecherGwt.getSession(), botName, packNumber, new AsyncCallbackAdapter<Void>("Mediator.getPack"));
                        }
                    } catch (NumberFormatException e) {
                        // Nothing
                    }
                }
                pack.setText("");
            }
        });
        final KeyUpHandler keyHandler = new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                    packButton.fireEvent(new SelectEvent());
                }
            }
        };
        pack.addKeyUpHandler(keyHandler);
        buttons.add(refresh);
        buttons.add(cancel);
        buttons.add(pack);
        buttons.add(packButton);
        buttons.setSpacing(2);
        panel.add(buttons);
        panel.add(progressBar);
        panel.add(filter);
        panel.add(pager2);
        panel.add(table);
        panel.add(pager1);
        initWidget(new ScrollPanel(panel));
        mediatorService.getList(BotLeecherGwt.getSession(), botName, false, new AsyncCallbackAdapter<Void>("Mediator.getList"));
    }

    @Override
    public boolean isValid(PackListEvent.Pack value, String filter) {
        return value.getName().toLowerCase().contains(filter.toLowerCase());
    }

    public void onCloseTab() {
        mediatorService.removeLeecher(BotLeecherGwt.getSession(), botName, new AsyncCallbackAdapter<Void>());
    }
}
