package botleecher.client.domain;

import java.io.Serializable;

public class SessionClient implements Serializable {
    private String user;
    private String uuid;

    public SessionClient(String user, String uuid) {
        this.user = user;
        this.uuid = uuid;
    }

    public SessionClient() {
    }

    public String getUser() {
        return user;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
