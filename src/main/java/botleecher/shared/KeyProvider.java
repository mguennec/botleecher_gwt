package botleecher.shared;

import com.sencha.gxt.data.shared.ModelKeyProvider;

public class KeyProvider implements ModelKeyProvider<String> {

    /**
     * Gets a key value that maps to this object. Keys must be consistent and
     * unique for a given model, as a database primary key would be used.
     */
    @Override
    public String getKey(String item) {
        return item;
    }
}
