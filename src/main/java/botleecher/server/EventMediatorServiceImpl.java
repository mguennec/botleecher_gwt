package botleecher.server;

import botleecher.client.event.DownloadStatusEvent;
import botleecher.client.event.MessageEvent;
import botleecher.client.event.PackListEvent;
import botleecher.server.utils.PackUtils;
import de.novanic.eventservice.client.event.domain.Domain;
import de.novanic.eventservice.client.event.domain.DomainFactory;
import de.novanic.eventservice.service.RemoteEventServiceServlet;
import fr.botleecher.rev.model.Pack;
import fr.botleecher.rev.service.EventMediatorService;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Maxime Guennec
 * Date: 30/07/13
 * Time: 00:04
 * To change this template use File | Settings | File Templates.
 */
public class EventMediatorServiceImpl extends RemoteEventServiceServlet implements EventMediatorService {

    final Domain DOMAIN = DomainFactory.getDomain("bot");

    @Override
    public void sendMessage(String message, MessageType type) {
        addEvent(DOMAIN, new MessageEvent(message, MessageEvent.MessageType.valueOf(type.name())));
    }

    @Override
    public void sendPack(String botName, List<Pack> packList) {
        addEvent(DOMAIN, new PackListEvent(botName, PackUtils.getClientPacks(packList)));
    }

    @Override
    public void sendUserList(List<String> users) {
        addEvent(DOMAIN, new botleecher.client.event.UserListEvent(users));
    }

    @Override
    public void sendTransferStatus(String botName, String fileName, int completion) {
        addEvent(DOMAIN, new DownloadStatusEvent(botName, fileName, completion));
    }
}
