package botleecher.server.utils;

import botleecher.client.event.PackListEvent;
import fr.botleecher.rev.model.Pack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Maxime Guennec
 * Date: 28/08/13
 * Time: 18:49
 * To change this template use File | Settings | File Templates.
 */
public class PackUtils {
    public static PackListEvent.Pack getClientPack(final Pack pack) {
        return new PackListEvent.Pack(pack.getId(), pack.getDownloads(), pack.getSize(), pack.getName(),pack.getStatus().toString());
    }

    public static List<PackListEvent.Pack> getClientPacks(final List<Pack> packs) {
        final List<PackListEvent.Pack> clientPacks = new ArrayList<PackListEvent.Pack>();
        for (Pack pack : packs) {
            clientPacks.add(getClientPack(pack));
        }

        return clientPacks;
    }

}
