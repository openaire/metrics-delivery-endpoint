package eu.openaire.mas.delivery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Object wrapper for a list of identifiers, useful so that at the HTTP API level
 * all JSON responses are objects.
 */
public class IdList {
    private final ArrayList<String> items;

    public List<String> getItems() {
	return items;
    }

    public IdList(Collection<String> items) {
	this.items = new ArrayList<>(items);
    }
}
