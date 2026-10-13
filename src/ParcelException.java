/** Base checked exception for all parcel-system errors. */
public class ParcelException extends Exception {
    private static final long serialVersionUID = 1L;

    public ParcelException(String message) {
        super(message);
    }
}
