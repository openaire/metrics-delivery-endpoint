package eu.openaire.mas.delivery.mapping;

/**
 * Exception indicating mapping was not found.
 * @author mhorst
 *
 */
public class MappingNotFoundException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -502313872187604506L;

    public MappingNotFoundException(String message) {
        super(message);
    }

}
