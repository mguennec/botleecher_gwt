package botleecher.client.event;

import de.novanic.eventservice.client.event.Event;

/**
 * Created with IntelliJ IDEA.
 * User: Maxime Guennec
 * Date: 21/09/13
 * Time: 21:14
 * To change this template use File | Settings | File Templates.
 */
public class DownloadStatusEvent implements Event {

    private String nick;
    private String fileName;
    private int completion;

    public DownloadStatusEvent(String botName, String fileName, int completion) {
        this.nick = botName;
        this.fileName = fileName;
        this.completion = completion;
    }

    public DownloadStatusEvent() {
        // Nothing
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getCompletion() {
        return completion;
    }

    public void setCompletion(int completion) {
        this.completion = completion;
    }
}
