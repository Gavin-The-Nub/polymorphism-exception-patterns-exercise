import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * SINGLETON PATTERN — the one and only store of parcels.
 * The constructor is private; use ParcelManager.getInstance().
 */
public class ParcelManager {

    private static ParcelManager instance;

    private final List<Parcel> parcels = new ArrayList<>();

    private ParcelManager() { }

    public static ParcelManager getInstance() {
        if (instance == null) {
            instance = new ParcelManager();
        }
        return instance;
    }

    // ── Operations ──────────────────────────────────────────────────────────

    public boolean exists(String trackingNumber) {
        for (Parcel p : parcels) {
            if (p.getTrackingNumber().equalsIgnoreCase(trackingNumber)) {
                return true;
            }
        }
        return false;
    }

    public void addParcel(Parcel parcel) throws DuplicateTrackingNumberException {
        if (exists(parcel.getTrackingNumber())) {
            throw new DuplicateTrackingNumberException(parcel.getTrackingNumber());
        }
        if (parcel.getDeliveryService() == null) {
            throw new IllegalStateException("Parcel must have a delivery service.");
        }
        parcels.add(parcel);
    }

    public Parcel findParcel(String trackingNumber) throws ParcelNotFoundException {
        for (Parcel p : parcels) {
            if (p.getTrackingNumber().equalsIgnoreCase(trackingNumber.trim())) {
                return p;
            }
        }
        throw new ParcelNotFoundException();
    }

    /** Finds a parcel that is still pending, or throws the matching exception. */
    public Parcel findPendingParcel(String trackingNumber)
            throws ParcelNotFoundException, ParcelAlreadyDeliveredException {
        Parcel parcel = findParcel(trackingNumber);
        if (parcel.isDelivered()) {
            throw new ParcelAlreadyDeliveredException(parcel.getTrackingNumber());
        }
        return parcel;
    }

    public void changeDeliveryService(Parcel parcel, DeliveryStrategy newService)
            throws SameDayNotAllowedException {
        parcel.setDeliveryService(newService); // old service kept if this throws
    }

    public void markDelivered(String trackingNumber)
            throws ParcelNotFoundException, ParcelAlreadyDeliveredException {
        findParcel(trackingNumber).markDelivered();
    }

    public List<Parcel> getAllParcels() {
        return Collections.unmodifiableList(parcels);
    }
}
