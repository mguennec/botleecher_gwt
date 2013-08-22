package botleecher.client.event;

import de.novanic.eventservice.client.event.Event;

/**
 * Created with IntelliJ IDEA.
 * User: Maxime Guennec
 * Date: 29/07/13
 * Time: 14:45
 * To change this template use File | Settings | File Templates.
 */
public class MessageEvent implements Event {

    private String message;

    private MessageType type;

    public MessageType getType() {
        return type;
    }

    public String getMessage() {
        final String message;
        switch (type) {
            case DOWNLOAD:
                message = "<span class=\"downloadMessage\">" + this.message + "</span>";
                break;
            case ADDED:
                message = "<span class=\"infoMessage\">" + this.message + "</span>";
                break;
            case ERROR:
                message = "<span class=\"errorMessage\">" + this.message + "</span>";
                break;
            case REQUEST:
                message = "<span class=\"requestMessage\">" + this.message + "</span>";
                break;
            default:
                message = this.message;
                break;
        }
        return message;
    }

    public MessageEvent(final String message, final MessageType type) {
        this.message = message;
        this.type = type;
    }

    public MessageEvent() {
    }

    public enum MessageType {
        DOWNLOAD, ADDED, ERROR, REQUEST;
    }
}
