/**
 * STRATEGY PATTERN — the common interface for all delivery services.
 * Each concrete strategy encapsulates its own fee formula, so a parcel's
 * delivery service can be swapped at runtime without changing Parcel code.
 */
public interface DeliveryStrategy {

    /** Display name, e.g. "Express". */
    String getName();

    /** Computes the delivery fee from the parcel's base fee. */
    double calculateFee(double baseFee);

    /** Whether this service can be used for the given parcel. Allowed by default. */
    default boolean isAllowedFor(Parcel parcel) {
        return true;
    }
}
