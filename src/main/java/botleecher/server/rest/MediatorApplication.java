package botleecher.server.rest;

import botleecher.server.rest.resources.MediatorResource;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Maxime Guennec
 * Date: 25/08/13
 * Time: 15:35
 * To change this template use File | Settings | File Templates.
 */
public class MediatorApplication extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        final Set<Class<?>> s = new HashSet<Class<?>>();
        s.add(MediatorResource.class);
        return s;
    }
}
