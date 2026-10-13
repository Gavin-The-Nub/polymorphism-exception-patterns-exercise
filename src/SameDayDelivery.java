/**
 * Concrete strategy: Delivery fee = (Base fee x 2) + Php 100.
 * Only allowed for parcels with a chargeable weight of 10 kg or less.
 */
public class SameDayDelivery implements DeliveryStrategy {

    private static final double MULTIPLIER   = 2.0;
    private static final double SURCHARGE    = 100.0;
    public  static final double WEIGHT_LIMIT = 10.0;

    @Override
    public String getName() { return "Same Day"; }

    @Override
    public double calculateFee(double baseFee) {
        return (baseFee * MULTIPLIER) + SURCHARGE;
    }

    @Override
    public boolean isAllowedFor(Parcel parcel) {
        return parcel.getChargeableWeight() <= WEIGHT_LIMIT;
    }
}
