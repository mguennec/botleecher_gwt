package botleecher.server.rest.resources;

import botleecher.server.module.BotLeecherModule;
import botleecher.server.security.SessionManager;
import fr.botleecher.rev.BotLeecher;
import fr.botleecher.rev.model.Pack;
import fr.botleecher.rev.service.BotMediator;
import org.apache.commons.lang3.StringUtils;
import org.pircbotx.User;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/bots")
public class MediatorResource {

    private BotMediator botMediator = BotLeecherModule.getInstance(BotMediator.class);
    private SessionManager sessionManager = BotLeecherModule.getInstance(SessionManager.class);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> getUserList() {
        final List<String> users = new ArrayList<String>();
        for (User user : botMediator.getUsers()) {
            users.add(user.getNick());
        }
        return users;
    }

    @GET
    @Path("/current")
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> getBots() {
        final List<String> bots = new ArrayList<String>();
        for (BotLeecher botLeecher : botMediator.getAllBots()) {
            bots.add(botLeecher.getUser().getNick());
        }
        return bots;
    }

    @PUT
    @Path("/{name}")
    public Response addBot(@Context final HttpServletRequest request, @PathParam("name") final String name, @CookieParam("sid") final String sid, @CookieParam("user") final String user) throws Exception {
        return new SecurityCallback(sid, user) {
            @Override
            public void callback() {
                botMediator.getList(name, true);
            }
        }.execute(request);
    }

    @GET
    @Path("/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Pack> getList(@PathParam("name") final String name) {
        return botMediator.getCurrentPackList(name);
    }

    @PUT
    @Path("/{name}/{id}")
    public Response getPack(@Context final HttpServletRequest request, @PathParam("name") final String name, @PathParam("id") final String id, @CookieParam("sid") final String sid, @CookieParam("user") final String user) throws Exception {
        return new SecurityCallback(sid, user) {
            @Override
            public void callback() {
                botMediator.getPack(name, Integer.valueOf(id));
            }
        }.execute(request);
    }

    private abstract class SecurityCallback {
        private final String sid;
        private final String user;

        protected SecurityCallback(String sid, String user) {
            this.sid = sid;
            this.user = user;
        }

        public abstract void callback();

        public Response execute(final HttpServletRequest request) throws Exception {

            final Response response = checkSession(request);
            if (Response.Status.OK.getStatusCode() == response.getStatus()) {
                callback();
            }

            return response;
        }

        private Response checkSession(final HttpServletRequest request) throws Exception {
            final Response response;
            final String sessionParam = request.getParameter("session");
            final String loginParam = request.getParameter("login");
            if (StringUtils.isNotBlank(sid) && StringUtils.isNotBlank(user) && sessionManager.checkSession(user, sid, request.getRemoteAddr())) {
                // The session on the header is valid
                response = Response.ok().build();
            } else if (StringUtils.isNotBlank(sessionParam) && StringUtils.isNotBlank(loginParam) && sessionManager.checkSession(loginParam, sessionParam, request.getRemoteAddr())) {
                response = Response.ok().cookie(new NewCookie[]{new NewCookie("sid", sessionParam), new NewCookie("user", loginParam)}).build();
            } else {
                response = Response.serverError().build();
            }
            return response;
        }
    }

}
