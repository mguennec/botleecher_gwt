package botleecher.client.event;

import de.novanic.eventservice.client.event.Event;

import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Maxime Guennec
 * Date: 29/07/13
 * Time: 14:45
 * To change this template use File | Settings | File Templates.
 */
public class PackListEvent implements Event {

    private List<Pack> packs;

    private String nick;

    public String getNick() {
        return nick;
    }

    public List<Pack> getPacks() {
        return packs;
    }

    public PackListEvent(final String nick, final List<Pack> packs) {
        this.packs = packs;
        this.nick = nick;
    }

    public PackListEvent() {
    }

    public static class Pack implements Serializable {

        private int id;
        private int downloads;
        private int size;
        private String name;

        private String status;


        public Pack(int id, int downloads, int size, String name, String status) {
            this.id = id;
            this.downloads = downloads;
            this.size = size;
            this.name = name;
            this.status = status;
        }

        public Pack() {
        }

        /**
         *
         * @param name
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         *
         * @return
         */
        public String getName() {
            return name;
        }

        /**
         *
         * @param downloads
         */
        public void setDownloads(int downloads) {
            this.downloads = downloads;
        }

        /**
         *
         * @return
         */
        public int getDownloads() {
            return downloads;
        }

        /**
         *
         * @param size
         */
        public void setSize(int size) {
            this.size = size;
        }

        /**
         *
         * @return
         */
        public int getSize() {
            return size;
        }

        /**
         *
         * @param id
         */
        public void setId(int id) {
            this.id = id;
        }

        /**
         *
         * @return
         */
        public int getId() {
            return id;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getStatus() {
            return status;
        }

        @Override
        public String toString() {
            return "Pack #"+id+", "+getSize()+"K, "+downloads+" downloads -> "+name;
        }
    }
}
