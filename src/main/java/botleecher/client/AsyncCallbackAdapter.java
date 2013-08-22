package botleecher.client;

import botleecher.client.event.MessageEvent;
import botleecher.shared.LoginException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sencha.gxt.core.client.util.Format;
import de.novanic.eventservice.client.event.RemoteEventService;
import de.novanic.eventservice.client.event.RemoteEventServiceFactory;
import de.novanic.eventservice.client.event.domain.DomainFactory;


public class AsyncCallbackAdapter<T> implements AsyncCallback<T> {

    private String serviceCall;

    private String failureMessageFormat;

    private static final String DEFAULT_FORMAT = "Service call {0} failed.";

    final RemoteEventService eventService = RemoteEventServiceFactory.getInstance().getRemoteEventService();

    public AsyncCallbackAdapter() {
        this("");
    }

    public AsyncCallbackAdapter(final String serviceCall) {
        this(serviceCall, DEFAULT_FORMAT);
    }

    public AsyncCallbackAdapter(final String serviceCall, final String failureMessageFormat) {
        this.serviceCall = serviceCall;
        this.failureMessageFormat = failureMessageFormat;
    }

    public String getFailureMessage() {
        return Format.substitute(failureMessageFormat, serviceCall);
    }

    /**
     * Called when an asynchronous call fails to complete normally.
     * {@link com.google.gwt.user.client.rpc.IncompatibleRemoteServiceException}s, {@link com.google.gwt.user.client.rpc.InvocationException}s,
     * or checked exceptions thrown by the service method are examples of the type
     * of failures that can be passed to this method.
     * <p/>
     * <p>
     * If <code>caught</code> is an instance of an
     * {@link com.google.gwt.user.client.rpc.IncompatibleRemoteServiceException} the application should try to
     * get into a state where a browser refresh can be safely done.
     * </p>
     *
     * @param caught failure encountered while executing a remote procedure call
     */
    @Override
    public void onFailure(Throwable caught) {
        eventService.addEvent(DomainFactory.getDomain("bot"), new MessageEvent(getFailureMessage(), MessageEvent.MessageType.ERROR));
        if (caught instanceof LoginException) {
            BotLeecherGwt.sessionExpired();
        }
    }

    /**
     * Called when an asynchronous call completes successfully.
     *
     * @param result the return value of the remote produced call
     */
    @Override
    public void onSuccess(T result) {
    }
}
