/**
 * A fragile parcel — a special kind of Box.
 * Max weight 30 kg, same chargeable-weight rule as Box,
 * base fee = Box base fee + Php 100 handling fee.
 */
public class Fragile extends Box {

    public static final double MAX_WEIGHT   = 30.0;
    public static final double HANDLING_FEE = 100.0;

    private final String handlingNote; // non-empty

    public Fragile(String trackingNumber, String receiverName, double weight,
                   int length, int width, int height, String handlingNote)
            throws InvalidInputException {
        super(trackingNumber, receiverName, weight, length, width, height);
        this.handlingNote = validateHandlingNote(handlingNote);
    }

    public static String validateHandlingNote(String note) throws InvalidInputException {
        if (note == null || note.trim().isEmpty()) {
            throw new InvalidInputException("Handling note must not be empty.");
        }
        return note.trim();
    }

    public String getHandlingNote() { return handlingNote; }

    @Override public String getType()      { return "Fragile"; }
    @Override public double getMaxWeight() { return MAX_WEIGHT; }

    @Override
    public double getBaseFee() {
        return super.getBaseFee() + HANDLING_FEE;
    }
}
