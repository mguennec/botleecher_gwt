package botleecher.server;

import botleecher.server.module.BotLeecherModule;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Created with IntelliJ IDEA.
 * User: Maxime Guennec
 * Date: 11/08/13
 * Time: 04:32
 * To change this template use File | Settings | File Templates.
 */
public class InjectedRemoteServiceServlet extends RemoteServiceServlet {

    public InjectedRemoteServiceServlet() {
        super();
        BotLeecherModule.getInjector().injectMembers(this);
    }

    /**
     * The wrapping constructor used by service implementations that are
     * separate from this class.  The servlet will delegate AJAX
     * requests to the appropriate method in the given object.
     */
    public InjectedRemoteServiceServlet(Object delegate) {
        super(delegate);
        BotLeecherModule.getInjector().injectMembers(this);
    }
}
