package botleecher.server;

import botleecher.client.MediatorService;
import botleecher.client.domain.SessionClient;
import botleecher.client.event.PackListEvent;
import botleecher.server.security.SessionManager;
import botleecher.server.utils.PackUtils;
import botleecher.shared.LoginException;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import fr.botleecher.rev.BotLeecher;
import fr.botleecher.rev.service.BotMediator;
import org.pircbotx.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@SuppressWarnings("GwtServiceNotRegistered")
@Singleton
public class MediatorServiceImpl extends RemoteServiceServlet implements MediatorService {

    @Inject
    private SessionManager sessionManager;
    @Inject
    private BotMediator botMediator;

    private boolean isSessionValid(final SessionClient session) {
        boolean retVal;
        try {
            retVal = sessionManager.checkSession(session.getUser(), session.getUuid(), getThreadLocalRequest() == null ? session.getIp() : getThreadLocalRequest().getRemoteAddr());
        } catch (Exception e) {
            retVal = false;
        }
        return retVal;
    }

    public void connect(final SessionClient session, final String server, final String channel) throws Exception {
        if (!isSessionValid(session)) {
            throw new LoginException(session);
        }
        botMediator.connect(server, channel);
    }

    public void createLeecher(final SessionClient session, String user) throws LoginException {
        if (!isSessionValid(session)) {
            throw new LoginException(session);
        }
        botMediator.createLeecher(user);
    }

    public void getList(final SessionClient session, String user) throws LoginException {
        if (!isSessionValid(session)) {
            throw new LoginException(session);
        }
        getList(session, user, true);
    }

    @Override
    public void getList(final SessionClient session, String user, boolean refresh) throws LoginException {
        if (!isSessionValid(session)) {
            throw new LoginException(session);
        }
        botMediator.getList(user, refresh);
    }

    @Override
    public List<PackListEvent.Pack> getCurrentList(SessionClient session, String user) throws LoginException {
        if (!isSessionValid(session)) {
            throw new LoginException(session);
        }
        return PackUtils.getClientPacks(botMediator.getCurrentPackList(user));
    }

    public void getPack(final SessionClient session, String user, int pack) throws LoginException {
        if (!isSessionValid(session)) {
            throw new LoginException(session);
        }
        botMediator.getPack(user, pack);
    }

    public void cancel(final SessionClient session, String user) throws LoginException {
        if (!isSessionValid(session)) {
            throw new LoginException(session);
        }
        botMediator.cancel(user);
    }

    public int getProgress(final SessionClient session, String user) throws LoginException {
        if (!isSessionValid(session)) {
            throw new LoginException(session);
        }
        return botMediator.getProgress(user);
    }

    public String getFileName(final SessionClient session, String user) throws LoginException {
        if (!isSessionValid(session)) {
            throw new LoginException(session);
        }
        return botMediator.getCurrentFile(user);
    }

    public long getTransfertRate(final SessionClient session, String user) throws LoginException {
        if (!isSessionValid(session)) {
            throw new LoginException(session);
        }
        return botMediator.getTransfertRate(user);
    }

    public Date getEstimatedEnd(final SessionClient session, String user) throws LoginException {
        if (!isSessionValid(session)) {
            throw new LoginException(session);
        }
        return botMediator.getEstimatedEnd(user);
    }

    @Override
    public String getSavePath(final SessionClient session) throws Exception {
        if (!isSessionValid(session)) {
            throw new LoginException(session);
        }
        return botMediator.getSaveDir();
    }

    @Override
    public void setSavePath(final SessionClient session, String path) throws Exception {
        if (!isSessionValid(session)) {
            throw new LoginException(session);
        }
        botMediator.setSaveDir(path);
    }

    @Override
    public List<String> getServers(final SessionClient session) throws Exception {
        if (!isSessionValid(session)) {
            throw new LoginException(session);
        }
        return botMediator.getServers();
    }

    @Override
    public List<String> getChannels(final SessionClient session) throws Exception {
        if (!isSessionValid(session)) {
            throw new LoginException(session);
        }
        return botMediator.getChannels();
    }

    @Override
    public void addServer(final SessionClient session, String server) throws Exception {
        if (!isSessionValid(session)) {
            throw new LoginException(session);
        }
        botMediator.addServer(server);
    }

    @Override
    public void addChannel(final SessionClient session, String channel) throws Exception {
        if (!isSessionValid(session)) {
            throw new LoginException(session);
        }
        botMediator.addChannel(channel);
    }

    @Override
    public List<String> getAllBots(final SessionClient session) throws LoginException {
        if (!isSessionValid(session)) {
            throw new LoginException(session);
        }
        final List<String> bots = new ArrayList<String>();
        for (BotLeecher botLeecher : botMediator.getAllBots()) {
            bots.add(botLeecher.getUser().getNick());
        }
        return bots;
    }

    @Override
    public List<String> getUsers(final SessionClient session) throws LoginException {
        if (!isSessionValid(session)) {
            throw new LoginException(session);
        }
        final List<String> users = new ArrayList<>();
        for (User user : botMediator.getUsers()) {
            users.add(user.getNick());
        }
        return users;
    }

    @Override
    public void setNicks(final SessionClient session, String nicks) throws Exception {
        if (!isSessionValid(session)) {
            throw new LoginException(session);
        }
        botMediator.setNicks(Arrays.asList(nicks.split(",")));
    }

    @Override
    public String getNicks(final SessionClient session) throws Exception {
        if (!isSessionValid(session)) {
            throw new LoginException(session);
        }
        final StringBuilder nicks = new StringBuilder();
        for (String nick : botMediator.getNicks()) {
            if (nicks.length() > 0) {
                nicks.append(",");
            }
            nicks.append(nick);
        }
        return nicks.toString();
    }

    @Override
    public void setKeywords(final SessionClient session, String keywords) throws Exception {
        if (!isSessionValid(session)) {
            throw new LoginException(session);
        }
        botMediator.setKeywords(Arrays.asList(keywords.split(",")));
    }

    @Override
    public String getKeywords(final SessionClient session) throws Exception {
        if (!isSessionValid(session)) {
            throw new LoginException(session);
        }
        final StringBuilder keywords = new StringBuilder();
        for (String keyword : botMediator.getKeywords()) {
            if (keywords.length() > 0) {
                keywords.append(",");
            }
            keywords.append(keyword);
        }
        return keywords.toString();
    }

    @Override
    public void removeLeecher(final SessionClient session, String user) throws LoginException {
        if (!isSessionValid(session)) {
            throw new LoginException(session);
        }
        botMediator.removeLeecher(user);
    }
}
