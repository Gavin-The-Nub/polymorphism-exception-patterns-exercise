/** Thrown when Same Day delivery is chosen for a parcel over 10 kg chargeable weight. */
public class SameDayNotAllowedException extends ParcelException {
    private static final long serialVersionUID = 1L;

    public SameDayNotAllowedException() {
        super("Same Day delivery is only for parcels up to 10 kg!");
    }
}
