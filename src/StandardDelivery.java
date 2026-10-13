/** Concrete strategy: Delivery fee = Base fee. */
public class StandardDelivery implements DeliveryStrategy {

    @Override
    public String getName() { return "Standard"; }

    @Override
    public double calculateFee(double baseFee) {
        return baseFee;
    }
}
