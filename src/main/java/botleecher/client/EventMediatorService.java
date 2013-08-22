package botleecher.client;

import botleecher.client.event.MessageEvent;
import botleecher.client.event.PackListEvent;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import de.novanic.eventservice.client.event.domain.Domain;
import de.novanic.eventservice.client.event.domain.DomainFactory;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Maxime Guennec
 * Date: 30/07/13
 * Time: 00:02
 * To change this template use File | Settings | File Templates.
 */
@RemoteServiceRelativePath("EventMediatorService")
public interface EventMediatorService extends RemoteService {

    final Domain DOMAIN = DomainFactory.getDomain("bot");
    void sendMessage(final String message, final MessageEvent.MessageType type);

    void sendPack(final String botName, final List<PackListEvent.Pack> packList);

    void sendUserList(final List<String> users);
}
