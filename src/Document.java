/**
 * A document parcel.
 * Max weight 0.5 kg, chargeable weight = actual weight, base fee = Php 50 flat.
 */
public class Document extends Parcel {

    public static final double MAX_WEIGHT = 0.5;
    public static final double BASE_FEE   = 50.0;

    private final int pages; // 1 to 100

    public Document(String trackingNumber, String receiverName, double weight, int pages)
            throws InvalidInputException {
        super(trackingNumber, receiverName, weight);
        this.pages = validatePages(pages);
    }

    public static int validatePages(int pages) throws InvalidInputException {
        if (pages < 1 || pages > 100) {
            throw new InvalidInputException("Number of pages must be from 1 to 100.");
        }
        return pages;
    }

    public int getPages() { return pages; }

    @Override public String getType()              { return "Document"; }
    @Override public double getMaxWeight()         { return MAX_WEIGHT; }
    @Override public double getChargeableWeight()  { return getWeight(); }
    @Override public double getBaseFee()           { return BASE_FEE; }
}
