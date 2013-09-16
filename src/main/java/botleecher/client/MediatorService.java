package botleecher.client;

import botleecher.client.domain.SessionClient;
import botleecher.client.event.PackListEvent;
import botleecher.shared.LoginException;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Maxime Guennec
 * Date: 29/07/13
 * Time: 14:25
 * To change this template use File | Settings | File Templates.
 */
@RemoteServiceRelativePath("MediatorService")
public interface MediatorService extends RemoteService {

    void connect(final SessionClient session, final String server, final String channel) throws Exception;

    void createLeecher(final SessionClient session, final String user) throws LoginException;

    void getList(final SessionClient session, final String user) throws LoginException;

    void getList(final SessionClient session, final String user, final boolean refresh) throws LoginException;

    List<PackListEvent.Pack> getCurrentList(final SessionClient session, String user) throws LoginException;

    void getPack(final SessionClient session, final String user, final int pack) throws LoginException;

    void cancel(final SessionClient session, final String user) throws LoginException;

    int getProgress(final SessionClient session, final String user) throws LoginException;

    String getFileName(final SessionClient session, final String user) throws LoginException;

    long getTransfertRate(final SessionClient session, final String user) throws LoginException;

    Date getEstimatedEnd(final SessionClient session, final String user) throws LoginException;

    String getSavePath(final SessionClient session) throws Exception;

    void setSavePath(final SessionClient session, final String path) throws Exception;

    List<String> getServers(final SessionClient session) throws Exception;

    List<String> getChannels(final SessionClient session) throws Exception;

    void addServer(final SessionClient session, final String server) throws Exception;

    void addChannel(final SessionClient session, final String channel) throws Exception;

    List<String> getAllBots(final SessionClient session) throws LoginException;

    List<String> getUsers(final SessionClient session) throws LoginException;

    void setNicks(final SessionClient session, final String nicks) throws Exception;

    String getNicks(final SessionClient session) throws Exception;

    void setKeywords(final SessionClient session, final String keywords) throws Exception;

    String getKeywords(final SessionClient session) throws Exception;

    void removeLeecher(final SessionClient session, final String user) throws LoginException;

    public static class App {
        private static MediatorServiceAsync ourInstance = GWT.create(MediatorService.class);

        public static synchronized MediatorServiceAsync getInstance() {
            return ourInstance;
        }
    }
}
