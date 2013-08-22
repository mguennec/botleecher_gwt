package botleecher.client.listener;

import botleecher.client.event.MessageEvent;
import botleecher.client.event.PackListEvent;
import botleecher.client.event.UserListEvent;
import de.novanic.eventservice.client.event.listener.RemoteEventListener;

/**
 * Created with IntelliJ IDEA.
 * User: Maxime Guennec
 * Date: 29/07/13
 * Time: 18:32
 * To change this template use File | Settings | File Templates.
 */
public interface BotLeecherListener extends RemoteEventListener {
    void onMessageEvent(MessageEvent event);
    void onPackListEvent(PackListEvent event);
    void onUserListEvent(UserListEvent event);

}
