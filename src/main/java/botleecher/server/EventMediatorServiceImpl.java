package botleecher.server;

import botleecher.client.EventMediatorService;
import botleecher.client.event.MessageEvent;
import botleecher.client.event.PackListEvent;
import de.novanic.eventservice.service.RemoteEventServiceServlet;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Maxime Guennec
 * Date: 30/07/13
 * Time: 00:04
 * To change this template use File | Settings | File Templates.
 */
public class EventMediatorServiceImpl extends RemoteEventServiceServlet implements EventMediatorService {


    @Override
    public void sendMessage(String message, MessageEvent.MessageType type) {
        addEvent(DOMAIN, new MessageEvent(message, type));
    }

    @Override
    public void sendPack(String botName, List<PackListEvent.Pack> packList) {
        addEvent(DOMAIN, new PackListEvent(botName, packList));
    }

    @Override
    public void sendUserList(List<String> users) {
        addEvent(DOMAIN, new botleecher.client.event.UserListEvent(users));
    }
}
