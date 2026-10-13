/**
 * Abstract base class for every parcel in the system.
 *
 * POLYMORPHISM: subclasses (Document, Box, Fragile) override
 * getMaxWeight(), getChargeableWeight(), getBaseFee() and getType(),
 * so the rest of the system can treat every parcel simply as a "Parcel".
 *
 * STRATEGY: the delivery fee is delegated to a DeliveryStrategy object,
 * which can be swapped at runtime (Change Delivery Service).
 */
public abstract class Parcel {

    private final String trackingNumber;   // "ABC" + 4 digits, unique
    private final String receiverName;     // non-empty
    private final double weight;           // kg, > 0 and <= max weight
    private DeliveryStrategy deliveryService;
    private ParcelStatus status;

    protected Parcel(String trackingNumber, String receiverName, double weight)
            throws InvalidInputException {
        this.trackingNumber = validateTrackingNumber(trackingNumber);
        this.receiverName   = validateReceiverName(receiverName);
        this.weight         = validateWeight(weight, getMaxWeight());
        this.status         = ParcelStatus.PENDING;
    }

    // ── Polymorphic behaviour (overridden by each parcel type) ──────────────

    /** Display name of the parcel type, e.g. "Document". */
    public abstract String getType();

    /** Maximum allowed actual weight in kg. */
    public abstract double getMaxWeight();

    /** Weight used for pricing, in kg. */
    public abstract double getChargeableWeight();

    /** Base fee in Php, before the delivery service is applied. */
    public abstract double getBaseFee();

    // ── Strategy usage ──────────────────────────────────────────────────────

    /** Delivery fee = the selected delivery strategy applied to the base fee. */
    public double getDeliveryFee() {
        if (deliveryService == null) {
            throw new IllegalStateException("No delivery service selected yet.");
        }
        return deliveryService.calculateFee(getBaseFee());
    }

    /**
     * Swaps the delivery strategy. The old service is kept if the new one
     * is not allowed for this parcel.
     */
    public void setDeliveryService(DeliveryStrategy service)
            throws SameDayNotAllowedException {
        if (!service.isAllowedFor(this)) {
            throw new SameDayNotAllowedException();
        }
        this.deliveryService = service;
    }

    public void markDelivered() throws ParcelAlreadyDeliveredException {
        if (status == ParcelStatus.DELIVERED) {
            throw new ParcelAlreadyDeliveredException(trackingNumber);
        }
        status = ParcelStatus.DELIVERED;
    }

    // ── Getters ─────────────────────────────────────────────────────────────

    public String getTrackingNumber()            { return trackingNumber; }
    public String getReceiverName()              { return receiverName; }
    public double getWeight()                    { return weight; }
    public DeliveryStrategy getDeliveryService() { return deliveryService; }
    public ParcelStatus getStatus()              { return status; }
    public boolean isDelivered()                 { return status == ParcelStatus.DELIVERED; }

    // ── Validation helpers (also used by Main to re-prompt per field) ───────

    public static String validateTrackingNumber(String value) throws InvalidInputException {
        String tn = value == null ? "" : value.trim().toUpperCase();
        if (!tn.matches("ABC\\d{4}")) {
            throw new InvalidInputException(
                "Tracking number must be 'ABC' followed by 4 digits (e.g. ABC0001).");
        }
        return tn;
    }

    public static String validateReceiverName(String value) throws InvalidInputException {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidInputException("Receiver name must not be empty.");
        }
        return value.trim();
    }

    public static double validateWeight(double weight, double maxWeight)
            throws InvalidInputException {
        if (weight <= 0) {
            throw new InvalidInputException("Weight must be greater than 0 kg.");
        }
        if (weight > maxWeight) {
            throw new InvalidInputException(
                String.format("Weight must not exceed %s kg for this parcel type.",
                              formatKg(maxWeight)));
        }
        return weight;
    }

    private static String formatKg(double kg) {
        return kg == Math.floor(kg) ? String.valueOf((int) kg) : String.valueOf(kg);
    }
}
