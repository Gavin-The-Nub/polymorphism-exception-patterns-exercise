/** Thrown when an operation needs a pending parcel but it is already delivered. */
public class ParcelAlreadyDeliveredException extends ParcelException {
    private static final long serialVersionUID = 1L;

    public ParcelAlreadyDeliveredException(String trackingNumber) {
        super("Parcel " + trackingNumber + " is already delivered.");
    }
}
