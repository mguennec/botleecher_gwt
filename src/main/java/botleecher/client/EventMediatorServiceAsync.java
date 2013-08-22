package botleecher.client;

import botleecher.client.event.MessageEvent;
import botleecher.client.event.PackListEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.List;

public interface EventMediatorServiceAsync {
    void sendMessage(final String message, final MessageEvent.MessageType type, AsyncCallback<Void> async);

    void sendUserList(final List<String> users, AsyncCallback<Void> async);

    void sendPack(final String botName, final List<PackListEvent.Pack> packList, AsyncCallback<Void> async);
}
