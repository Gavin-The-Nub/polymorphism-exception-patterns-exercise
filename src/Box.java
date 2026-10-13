/**
 * A box parcel.
 * Max weight 50 kg.
 * Volumetric weight = (L x W x H) / 3500.
 * Chargeable weight = max(actual, volumetric), rounded UP to the next whole kg.
 * Base fee = Php 40 x chargeable weight.
 */
public class Box extends Parcel {

    public static final double MAX_WEIGHT         = 50.0;
    public static final double RATE_PER_KG        = 40.0;
    public static final double VOLUMETRIC_DIVISOR = 3500.0;

    private final int length; // cm, > 0
    private final int width;  // cm, > 0
    private final int height; // cm, > 0

    public Box(String trackingNumber, String receiverName, double weight,
               int length, int width, int height) throws InvalidInputException {
        super(trackingNumber, receiverName, weight);
        this.length = validateDimension(length, "Length");
        this.width  = validateDimension(width,  "Width");
        this.height = validateDimension(height, "Height");
    }

    public static int validateDimension(int value, String label) throws InvalidInputException {
        if (value <= 0) {
            throw new InvalidInputException(label + " must be a whole number greater than 0 cm.");
        }
        return value;
    }

    public double getVolumetricWeight() {
        return ((double) length * width * height) / VOLUMETRIC_DIVISOR;
    }

    public int getLength() { return length; }
    public int getWidth()  { return width; }
    public int getHeight() { return height; }

    @Override public String getType()      { return "Box"; }
    @Override public double getMaxWeight() { return MAX_WEIGHT; }

    @Override
    public double getChargeableWeight() {
        return Math.ceil(Math.max(getWeight(), getVolumetricWeight()));
    }

    @Override
    public double getBaseFee() {
        return RATE_PER_KG * getChargeableWeight();
    }
}
