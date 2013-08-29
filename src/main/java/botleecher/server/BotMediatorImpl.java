/*
 * BotMediator.java
 *
 * Created on February 22, 2007, 10:21 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package botleecher.server;

import botleecher.client.EventMediatorService;
import botleecher.server.utils.PackUtils;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import fr.botleecher.rev.BotLeecher;
import fr.botleecher.rev.IrcConnection;
import fr.botleecher.rev.model.Pack;
import fr.botleecher.rev.model.PackList;
import fr.botleecher.rev.service.Settings;
import fr.botleecher.rev.tools.DualOutputStream;
import org.pircbotx.User;
import org.pircbotx.exception.IrcException;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.*;

/**
 *
 * @author francisdb
 */
@Singleton
public class BotMediatorImpl extends ListenerAdapter implements fr.botleecher.rev.service.BotMediator {

    private static final Logger LOGGER = LoggerFactory.getLogger(BotMediatorImpl.class);
    private IrcConnection ircConnection;

    @Inject
    private Injector injector;

    @Inject
    private Settings settings;

    private String server;
    private String channel;

    private Map<String, User> users = new HashMap<String, User>();

    @Inject
    private EventMediatorService service;

    public void disconnected() {
        // Nothing
    }

    @Override
    public List<BotLeecher> getAllBots() {
        return ircConnection == null ? new ArrayList<BotLeecher>() : ircConnection.getAllBots();
    }

    @Override
    public void onMessage(MessageEvent event) throws Exception {
        final String message = event.getMessage();
        if (message != null) {
            for (String keyword : settings.getKeywords()) {
                if (message.toLowerCase().contains(keyword.toLowerCase())) {
                    writeText(event.getUser().getNick() + " : " + message);
                    break;
                }
            }
        }
    }
    @Override
    public void onNotice(NoticeEvent event) {
        if (ircConnection != null) {
            BotLeecher leecher = getIrcConnection().getBotLeecher(event.getUser().getNick());
            if (leecher != null) {
                leecher.onNotice(event.getNotice());
            }
        }
    }

    @Override
    public void onIncomingFileTransfer(final IncomingFileTransferEvent event) {
       new Thread(new Runnable() {
            public void run() {
                if (getIrcConnection() != null) {
                    writeText(event.getUser().getNick() + " : Downloading " + event.getSafeFilename(), botleecher.client.event.MessageEvent.MessageType.DOWNLOAD);
                     getIrcConnection().getBotLeecher(event.getUser().getNick()).onIncomingFileTransfer(event);
                    writeText(event.getUser().getNick() + " : Download complete " + event.getSafeFilename(), botleecher.client.event.MessageEvent.MessageType.DOWNLOAD);
                }
            }
        }).start();
    }

    @Override
    public void onUserList(UserListEvent event) {
        final ArrayList<User> list = new ArrayList<User>(event.getUsers());
        Collections.sort(list, new UserComparator());
        userListLoaded(event.getChannel().getName(), list);
    }
    /**
     *
     */
    @Override
    public void onDisconnect(DisconnectEvent event) {
        LOGGER.info("DISCONNECT:\tDisconnected from server");
        disconnected();
    }

    public BotMediatorImpl() {
        redirectOutputStreams();
    }

    
    private void redirectOutputStreams(){
        PrintStream oldStream = System.out;
        PrintStream aPrintStream = new PrintStream(new DualOutputStream(oldStream, this));
        System.setOut(aPrintStream); // catches System.out messages
        System.setErr(aPrintStream); // catches error messages
    }

    @Override
    public void userListLoaded(final String channel, final List<User> users) {
        final List<String> userList = new ArrayList<String>();
        this.users.clear();
        for (User user : users) {
            userList.add(user.getNick());
            this.users.put(user.getNick(), user);
        }
        service.sendUserList(userList);
        //service.addEvent(DomainFactory.getDomain("bot"), new botleecher.client.event.UserListEvent(userList));
    }

    @Override
    public List<User> getUsers() {
        final ArrayList<User> list = new ArrayList<User>(users.values());
        Collections.sort(list, new UserComparator());
        return list;
    }

    public void writeText(final String text, final botleecher.client.event.MessageEvent.MessageType type) {
        service.sendMessage(text, type);
    }

    @Override
    public void writeText(final String text) {
        writeText(text, botleecher.client.event.MessageEvent.MessageType.INFO);
    }

    /**
     * Connects to the irc network
     */
    @Override
    public void connect(final String server, final String channel) throws InterruptedException {
        if (ircConnection != null) {
            ircConnection.shutdown();
            ircConnection = null;
        }
        if (!getServers().contains(server)) {
            addServer(server);
        }
        if (!getChannels().contains(channel)) {
            addChannel(channel);
        }
        this.server = server;
        this.channel = channel;
        ircConnection = injector.getInstance(IrcConnection.class);
        new Thread(new Runnable() {
            public void run() {
                try {
                    ircConnection.startBot();
                } catch (IOException e) {
                    LOGGER.error(e.getMessage(), e);
                } catch (IrcException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }).start();
    }

    @Override
    public void getList(final String user, final boolean refresh) {
        BotLeecher botLeecher = ircConnection.getBotLeecher(user);
        if (botLeecher == null) {
            createLeecher(user);
            botLeecher = ircConnection.getBotLeecher(user);
        }
        if (botLeecher != null) {
            if (refresh || botLeecher.getPackList() == null) {
                botLeecher.requestPackList();
            } else {
                packListLoaded(user, botLeecher.getPackList().getPacks());
            }
        }
    }

    @Override
    public List<Pack> getCurrentPackList(String user) {
        final List<Pack> packs = new ArrayList<Pack>();
        final BotLeecher botLeecher = ircConnection.getBotLeecher(user);
        if (botLeecher != null) {
            final PackList packList = botLeecher.getPackList();
            if (packList != null) {
                packs.addAll(packList.getPacks());
            }
        }
        return packs;
    }

    @Override
    public void getPack(final String user, final int pack) {
        final BotLeecher botLeecher = ircConnection.getBotLeecher(user);
        if (botLeecher != null) {
            writeText(user + " : Sending Request for pack #" + pack, botleecher.client.event.MessageEvent.MessageType.REQUEST);
            botLeecher.requestPack(pack);
        }
    }

    @Override
    public int getProgress(final String user) {
        final BotLeecher botLeecher = ircConnection == null ? null :  ircConnection.getBotLeecher(user);
        final int progress;
        if (botLeecher != null && botLeecher.getCurrentTransfer() != null) {
            progress = (int) (((double)botLeecher.getCurrentState() / (double)botLeecher.getFileSize()) * 100);
        } else {
            progress = 0;
        }
        return progress;
    }

    @Override
    public long getTransfertRate(final String user) {
        final BotLeecher botLeecher = ircConnection == null ? null :  ircConnection.getBotLeecher(user);
        final long rate;
        if (botLeecher != null && botLeecher.getCurrentTransfer() != null) {
            rate = botLeecher.getTransfertRate();
        } else {
            rate = 0;
        }
        return rate;
    }

    @Override
    public Date getEstimatedEnd(final String user) {
        final BotLeecher botLeecher = ircConnection == null ? null :  ircConnection.getBotLeecher(user);
        final Date end;
        if (botLeecher != null && botLeecher.getCurrentTransfer() != null) {
            end = botLeecher.getEstimatedEnd();
        } else {
            end = null;
        }
        return end;
    }

    @Override
    public List<String> getServers() {
        return settings.getServers();
    }

    @Override
    public void addServer(final String server) {
        settings.addServer(server);
    }

    @Override
    public List<String> getChannels() {
        return settings.getChannels();
    }

    @Override
    public void addChannel(final String channel) {
        settings.addChannel(channel);
    }

    @Override
    public String getSaveDir() {
        return settings.getSaveFolder().getAbsolutePath();
    }

    @Override
    public void setSaveDir(final String path) {
        settings.setSaveFolder(path);
    }

    @Override
    public List<String> getNicks() {
        return settings.getNicks();
    }

    @Override
    public void setNicks(final String nicks) {
        settings.setNicks(nicks);
    }

    @Override
    public List<String> getKeywords() {
        return settings.getKeywords();
    }

    @Override
    public void setKeywords(final String keywords) {
        settings.setKeywords(keywords);
    }

    @Override
    public String getCurrentFile(final String user) {
        final BotLeecher botLeecher = ircConnection == null ? null : ircConnection.getBotLeecher(user);
        final String file;
        if (botLeecher != null && botLeecher.getCurrentTransfer() != null && botLeecher.getCurrentTransfer().getFile() != null) {
            file = botLeecher.getCurrentTransfer().getFile().getName();
        } else {
            file = "";
        }
        return file;
    }

    @Override
    public void cancel(final String user) {
        final BotLeecher botLeecher = ircConnection == null ? null :  ircConnection.getBotLeecher(user);
        if (botLeecher != null) {
            botLeecher.cancel();
        }
    }

    @Override
    public void createLeecher(final String user) {
        final User userObject = users.get(user);
        if (userObject != null) {
            ircConnection.makeLeecher(userObject);
        }
    }

    /**
     * Todo refactor, this should stay private
     * @return 
     */
    private IrcConnection getIrcConnection() {
        return ircConnection;
    }

    @Override
    public String getServer() {
        return server;
    }

    @Override
    public String getChannel() {
        return channel;
    }

    @Override
    public void removeLeecher(final String user) {
        final User userObject = users.get(user);
        if (userObject != null) {
            ircConnection.removeLeecher(userObject);
        }
    }

    /**
     * Triggered when the pack list has been loaded
     *
     * @param packList
     */
    @Override
    public void packListLoaded(final String botName, List<Pack> packList) {
        service.sendPack(botName, PackUtils.getClientPacks(packList));
    }


    private static class UserComparator implements Comparator<User>, Serializable {

        public int compare(User o1, User o2) {
            return o1.getNick().compareToIgnoreCase(o2.getNick());
        }
    }

}
