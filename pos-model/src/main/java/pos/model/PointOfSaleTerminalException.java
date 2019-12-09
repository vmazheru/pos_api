package pos.model;

/**
 * Report issues related to the POS terminal
 */
@SuppressWarnings("serial")
public class PointOfSaleTerminalException extends RuntimeException {
    
    public PointOfSaleTerminalException(String message) {
        super(message);
    }
    
    public PointOfSaleTerminalException(Throwable cause) {
        super(cause);
    }

}
