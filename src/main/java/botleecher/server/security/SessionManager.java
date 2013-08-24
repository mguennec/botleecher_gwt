package botleecher.server.security;

import org.jdom2.JDOMException;

import java.io.IOException;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Maxime Guennec
 * Date: 10/08/13
 * Time: 23:20
 * To change this template use File | Settings | File Templates.
 */
public interface SessionManager {
    long SESSION_DURATION = 1000 * 60 * 60 * 24 * 7;
    boolean checkSession(String user, String uuid, String ip) throws Exception;

    String createSession(String user, String ip) throws Exception;

    void deleteSession(String sessionId) throws JDOMException, IOException;

    void deleteSessionsByUser(String login) throws JDOMException, IOException;

    public class Session {
        private String user;
        private String uuid;
        private String ip;
        private Date start;
        private Date end;

        public Session(String user, String uuid, String ip, Date start, Date end) {
            this.user = user;
            this.uuid = uuid;
            this.ip = ip;
            this.start = start;
            this.end = end;
        }

        public String getUser() {
            return user;
        }

        public Date getStart() {
            return start;
        }

        public Date getEnd() {
            return end;
        }

        public String getUuid() {
            return uuid;
        }

        public String getIp() {
            return ip;
        }
    }
}
