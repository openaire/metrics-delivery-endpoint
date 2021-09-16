package eu.openaire.mas.delivery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Object wrapper for a list, used so that at the HTTP API level
 * all JSON responses are objects.
 */
public class ItemList<T> {
    private final ArrayList<T> items;

    public List<T> getItems() {
	return items;
    }

    public ItemList(Collection<T> items) {
	this.items = new ArrayList<>(items);
    }
}
