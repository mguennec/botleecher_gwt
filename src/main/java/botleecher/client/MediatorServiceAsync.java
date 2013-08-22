package botleecher.client;

import botleecher.client.domain.SessionClient;
import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.Date;
import java.util.List;

public interface MediatorServiceAsync {
    void connect(final SessionClient session, final String server, final String channel, AsyncCallback<Void> async);

    void createLeecher(final SessionClient session, final String user, AsyncCallback<Void> async);

    void getList(final SessionClient session, final String user, AsyncCallback<Void> async);

    void getList(final SessionClient session, final String user, final boolean refresh, AsyncCallback<Void> async);

    void getPack(final SessionClient session, final String user, final int pack, AsyncCallback<Void> async);

    void cancel(final SessionClient session, final String user, AsyncCallback<Void> async);

    void getProgress(final SessionClient session, final String user, AsyncCallback<Integer> async);

    void getFileName(final SessionClient session, final String user, AsyncCallback<String> async);

    void getTransfertRate(final SessionClient session, final String user, AsyncCallback<Long> async);

    void getEstimatedEnd(final SessionClient session, final String user, AsyncCallback<Date> async);

    void getSavePath(final SessionClient session, AsyncCallback<String> async);

    void setSavePath(final SessionClient session, final String path, AsyncCallback<Void> async);

    void getServers(final SessionClient session, AsyncCallback<List<String>> async);

    void getChannels(final SessionClient session, AsyncCallback<List<String>> async);

    void addServer(final SessionClient session, final String server, AsyncCallback<Void> async);

    void addChannel(final SessionClient session, final String channel, AsyncCallback<Void> async);

    void getAllBots(final SessionClient session, AsyncCallback<List<String>> async);

    void getUsers(final SessionClient session, AsyncCallback<List<String>> async);

    void setNicks(final SessionClient session, final String nicks, AsyncCallback<Void> async);

    void getNicks(final SessionClient session, AsyncCallback<String> async);

    void setKeywords(final SessionClient session, final String keywords, AsyncCallback<Void> async);

    void getKeywords(final SessionClient session, AsyncCallback<String> async);

    void removeLeecher(final SessionClient session, final String user, AsyncCallback<Void> async);
}
