package botleecher.client.event;

import de.novanic.eventservice.client.event.Event;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Maxime Guennec
 * Date: 29/07/13
 * Time: 14:45
 * To change this template use File | Settings | File Templates.
 */
public class UserListEvent implements Event {

    private List<String> users;

    public List<String> getUsers() {
        return users;
    }

    public UserListEvent(final List<String> users) {
        this.users = users;
    }

    public UserListEvent() {
    }
}
