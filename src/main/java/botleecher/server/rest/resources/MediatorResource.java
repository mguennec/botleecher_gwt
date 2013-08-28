package botleecher.server.rest.resources;

import botleecher.client.MediatorService;
import botleecher.client.domain.SessionClient;
import botleecher.client.event.PackListEvent;
import botleecher.shared.LoginException;
import com.google.inject.Inject;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/bots")
public class MediatorResource {

    public static final String COMMAND_EXECUTED = "command executed";

    @Inject
    private MediatorService mediatorService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserList(@Context final HttpServletRequest request, @CookieParam("sid") final String sid, @CookieParam("user") final String user) throws Exception {
        return new SecurityCallback<List<String>>(sid, user, request.getRemoteAddr()) {
            @Override
            public List<String> callback(final SessionClient session) throws LoginException {
                return mediatorService.getUsers(session);
            }
        }.execute(request);
    }

    @GET
    @Path("/current")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBots(@Context final HttpServletRequest request, @CookieParam("sid") final String sid, @CookieParam("user") final String user) throws Exception {
        return new SecurityCallback<List<String>>(sid, user, request.getRemoteAddr()) {
            @Override
            public List<String> callback(final SessionClient session) throws LoginException {
                return mediatorService.getAllBots(session);
            }
        }.execute(request);
    }

    @PUT
    @Path("/{name}")
    public Response addBot(@Context final HttpServletRequest request, @PathParam("name") final String name, @CookieParam("sid") final String sid, @CookieParam("user") final String user) throws Exception {
        return new SecurityCallback(sid, user, request.getRemoteAddr()) {
            @Override
            public Object callback(final SessionClient session) throws LoginException {
                mediatorService.getList(session, name, true);
                return COMMAND_EXECUTED;
            }
        }.execute(request);
    }

    @GET
    @Path("/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getList(@Context final HttpServletRequest request, @PathParam("name") final String name, @CookieParam("sid") final String sid, @CookieParam("user") final String user) throws Exception {
        return new SecurityCallback<List<PackListEvent.Pack>>(sid, user, request.getRemoteAddr()) {
            @Override
            public List<PackListEvent.Pack> callback(final SessionClient session) throws LoginException {
                return mediatorService.getCurrentList(session, name);
            }
        }.execute(request);
    }

    @PUT
    @Path("/{name}/{id}")
    public Response getPack(@Context final HttpServletRequest request, @PathParam("name") final String name, @PathParam("id") final String id, @CookieParam("sid") final String sid, @CookieParam("user") final String user) throws Exception {
        return new SecurityCallback(sid, user, request.getRemoteAddr()) {
            @Override
            public Object callback(final SessionClient session) throws LoginException {
                mediatorService.getPack(session, name, Integer.valueOf(id));
                return COMMAND_EXECUTED;
            }
        }.execute(request);
    }

    private abstract class SecurityCallback<T> {
        private final SessionClient session;

        private SecurityCallback(String sid, String user, String ip) {
            session = new SessionClient();
            session.setUser(user);
            session.setUuid(sid);
            session.setIp(ip);
        }

        public abstract T callback(final SessionClient session) throws LoginException;

        public Response execute(final HttpServletRequest request) throws Exception {
            Response response;

            try {
                response = Response.ok(callback(session)).build();
            } catch (LoginException e) {
                response = Response.serverError().build();
            }

            return response;
        }
    }

}
