import java.util.List;
import java.util.Scanner;

/**
 * Entry point for the Parcel Delivery System.
 * Provides the interactive console menu.
 */
public class Main {

    private static final ParcelManager manager = ParcelManager.getInstance();
    private static final Scanner       scanner = new Scanner(System.in);

    /** Reads and validates one piece of input; throws if the input is invalid. */
    @FunctionalInterface
    private interface InputParser<T> {
        T parse(String input) throws ParcelException;
    }

    public static void main(String[] args) {
        printBanner();
        boolean running = true;

        while (running) {
            printMenu();
            String choice = readLine("Enter choice: ");

            switch (choice) {
                case "1" -> addParcel();
                case "2" -> viewAllParcels();
                case "3" -> changeDeliveryService();
                case "4" -> markAsDelivered();
                case "5" -> {
                    System.out.println("\nThank you for using the Parcel Delivery System. Goodbye!");
                    running = false;
                }
                default -> System.out.println("\nInvalid option. Please enter 1-5.");
            }
        }

        scanner.close();
    }

    // ── Menu builders ───────────────────────────────────────────────────────

    private static void printBanner() {
        System.out.println("================================");
        System.out.println("     PARCEL DELIVERY SYSTEM");
        System.out.println("================================");
    }

    private static void printMenu() {
        System.out.println("\n--- Main Menu ---");
        System.out.println("  1. Add Parcel");
        System.out.println("  2. View All Parcels");
        System.out.println("  3. Change Delivery Service");
        System.out.println("  4. Mark as Delivered");
        System.out.println("  5. Exit");
        System.out.println("-----------------");
    }

    // ── 1. Add Parcel ───────────────────────────────────────────────────────

    private static void addParcel() {
        System.out.println("\n--- Add Parcel ---");
        System.out.println("Select parcel type:");
        System.out.println("  1. Document");
        System.out.println("  2. Box");
        System.out.println("  3. Fragile");
        int type = prompt("Enter type (1-3): ", in -> parseIntInRange(in, 1, 3, "Parcel type"));

        // Common fields
        String trackingNumber = prompt("Enter Tracking Number (e.g. ABC0001): ", in -> {
            String tn = Parcel.validateTrackingNumber(in);
            if (manager.exists(tn)) {
                throw new DuplicateTrackingNumberException(tn);
            }
            return tn;
        });
        String receiver = prompt("Enter Receiver Name: ", Parcel::validateReceiverName);

        double maxWeight = switch (type) {
            case 1  -> Document.MAX_WEIGHT;
            case 2  -> Box.MAX_WEIGHT;
            default -> Fragile.MAX_WEIGHT;
        };
        double weight = prompt("Enter Weight in kg (max " + maxWeight + "): ",
                in -> Parcel.validateWeight(parseDouble(in, "Weight"), maxWeight));

        // Type-specific fields + construction
        Parcel parcel;
        try {
            if (type == 1) {
                int pages = prompt("Enter Number of Pages (1-100): ",
                        in -> Document.validatePages(parseInt(in, "Number of pages")));
                parcel = new Document(trackingNumber, receiver, weight, pages);
            } else {
                int length = promptDimension("Length");
                int width  = promptDimension("Width");
                int height = promptDimension("Height");
                if (type == 2) {
                    parcel = new Box(trackingNumber, receiver, weight, length, width, height);
                } else {
                    String note = prompt("Enter Handling Note: ", Fragile::validateHandlingNote);
                    parcel = new Fragile(trackingNumber, receiver, weight,
                                         length, width, height, note);
                }
            }
        } catch (InvalidInputException e) {
            // Should not happen since each field is validated above, but handled just in case.
            System.out.println("Could not create parcel: " + e.getMessage());
            return;
        }

        // Delivery service — re-select until allowed
        while (true) {
            DeliveryStrategy service = selectDeliveryService();
            try {
                parcel.setDeliveryService(service);
                break;
            } catch (SameDayNotAllowedException e) {
                System.out.println(e.getMessage());
            }
        }

        try {
            manager.addParcel(parcel);
            System.out.printf("Parcel added successfully! Delivery fee: Php %.2f%n",
                              parcel.getDeliveryFee());
        } catch (DuplicateTrackingNumberException e) {
            System.out.println(e.getMessage());
        }
    }

    private static int promptDimension(String label) {
        return prompt("Enter " + label + " in cm (max " + Box.MAX_DIMENSION + "): ",
                in -> Box.validateDimension(parseInt(in, label), label));
    }

    // ── 2. View All Parcels ─────────────────────────────────────────────────

    private static void viewAllParcels() {
        System.out.println("\n--- All Parcels ---");
        List<Parcel> parcels = manager.getAllParcels();
        if (parcels.isEmpty()) {
            System.out.println("No parcels recorded yet.");
            return;
        }

        String format = "| %-9s | %-8s | %-20s | %11s | %-8s | %13s | %-9s |%n";
        String line   = "+-----------+----------+----------------------+-------------+"
                      + "----------+---------------+-----------+";

        System.out.println(line);
        System.out.printf(format, "Tracking", "Type", "Receiver", "Chg. Weight",
                          "Service", "Delivery Fee", "Status");
        System.out.println(line);
        for (Parcel p : parcels) {   // polymorphic calls: getType(), getChargeableWeight(), ...
            System.out.printf(format,
                    p.getTrackingNumber(),
                    p.getType(),
                    truncate(p.getReceiverName(), 20),
                    String.format("%.2f kg", p.getChargeableWeight()),
                    p.getDeliveryService().getName(),
                    String.format("Php %.2f", p.getDeliveryFee()),
                    p.getStatus());
        }
        System.out.println(line);
    }

    // ── 3. Change Delivery Service ──────────────────────────────────────────

    private static void changeDeliveryService() {
        System.out.println("\n--- Change Delivery Service ---");
        String tn = readLine("Enter Tracking Number: ");
        try {
            Parcel parcel = manager.findPendingParcel(tn);
            System.out.println("Current service: " + parcel.getDeliveryService().getName());
            DeliveryStrategy newService = selectDeliveryService();
            manager.changeDeliveryService(parcel, newService);
            System.out.printf("Delivery service changed! New fee: Php %.2f%n",
                              parcel.getDeliveryFee());
        } catch (ParcelNotFoundException e) {
            System.out.println("Parcel not found!");
        } catch (ParcelAlreadyDeliveredException e) {
            System.out.println("Parcel already delivered!");
        } catch (SameDayNotAllowedException e) {
            System.out.println(e.getMessage()); // parcel keeps its old service
        }
    }

    // ── 4. Mark as Delivered ────────────────────────────────────────────────

    private static void markAsDelivered() {
        System.out.println("\n--- Mark as Delivered ---");
        String tn = readLine("Enter Tracking Number: ");
        try {
            manager.markDelivered(tn);
            System.out.println("Parcel delivered successfully!");
        } catch (ParcelNotFoundException e) {
            System.out.println("Parcel not found!");
        } catch (ParcelAlreadyDeliveredException e) {
            System.out.println("Parcel was already delivered.");
        }
    }

    // ── Shared helpers ──────────────────────────────────────────────────────

    private static DeliveryStrategy selectDeliveryService() {
        System.out.println("Select delivery service:");
        System.out.println("  1. Standard");
        System.out.println("  2. Express");
        System.out.println("  3. Same Day (up to 10 kg chargeable weight)");
        int choice = prompt("Enter service (1-3): ",
                in -> parseIntInRange(in, 1, 3, "Delivery service"));
        return switch (choice) {
            case 1  -> new StandardDelivery();
            case 2  -> new ExpressDelivery();
            default -> new SameDayDelivery();
        };
    }

    /** Keeps asking until the parser accepts the input. */
    private static <T> T prompt(String message, InputParser<T> parser) {
        while (true) {
            String input = readLine(message);
            try {
                return parser.parse(input);
            } catch (ParcelException e) {
                System.out.println("  x " + e.getMessage());
            }
        }
    }

    private static String readLine(String message) {
        System.out.print(message);
        if (!scanner.hasNextLine()) {           // input stream closed (e.g. Ctrl+D)
            System.out.println("\nInput closed. Exiting.");
            System.exit(0);
        }
        return scanner.nextLine().trim();
    }

    private static int parseInt(String input, String label) throws InvalidInputException {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new InvalidInputException(label + " must be a whole number.");
        }
    }

    private static int parseIntInRange(String input, int min, int max, String label)
            throws InvalidInputException {
        int value = parseInt(input, label);
        if (value < min || value > max) {
            throw new InvalidInputException(label + " must be from " + min + " to " + max + ".");
        }
        return value;
    }

    private static double parseDouble(String input, String label) throws InvalidInputException {
        try {
            double value = Double.parseDouble(input);
            if (Double.isNaN(value) || Double.isInfinite(value)) {
                throw new NumberFormatException();
            }
            return value;
        } catch (NumberFormatException e) {
            throw new InvalidInputException(label + " must be a number.");
        }
    }

    private static String truncate(String s, int max) {
        return s.length() <= max ? s : s.substring(0, max - 3) + "...";
    }
}
