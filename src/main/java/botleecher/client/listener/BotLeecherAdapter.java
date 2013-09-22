package botleecher.client.listener;

import botleecher.client.event.DownloadStatusEvent;
import botleecher.client.event.MessageEvent;
import botleecher.client.event.PackListEvent;
import botleecher.client.event.UserListEvent;
import de.novanic.eventservice.client.event.Event;

/**
 * Created with IntelliJ IDEA.
 * User: Maxime Guennec
 * Date: 29/07/13
 * Time: 18:33
 * To change this template use File | Settings | File Templates.
 */
public class BotLeecherAdapter implements BotLeecherListener {
    @Override
    public void onMessageEvent(MessageEvent event) {
    }

    @Override
    public void onPackListEvent(PackListEvent event) {
    }

    @Override
    public void onUserListEvent(UserListEvent event) {
    }

    @Override
    public void onDownloadStatusEvent(DownloadStatusEvent event) {
    }

    public void apply(Event event) {
        if (event instanceof MessageEvent) {
            onMessageEvent((MessageEvent) event);
        } else if (event instanceof PackListEvent) {
            onPackListEvent((PackListEvent) event);
        } else if (event instanceof UserListEvent) {
            onUserListEvent((UserListEvent) event);
        } else if (event instanceof DownloadStatusEvent) {
            onDownloadStatusEvent((DownloadStatusEvent) event);
        }
    }
}
