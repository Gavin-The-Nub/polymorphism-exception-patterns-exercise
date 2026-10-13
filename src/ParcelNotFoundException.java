/** Thrown when no parcel matches the given tracking number. */
public class ParcelNotFoundException extends ParcelException {
    private static final long serialVersionUID = 1L;

    public ParcelNotFoundException() {
        super("Parcel not found!");
    }
}
