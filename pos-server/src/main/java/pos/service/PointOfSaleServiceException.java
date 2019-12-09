package pos.service;

/**
 * Report issues related to {@link PointOfSaleService}
 */
@SuppressWarnings("serial")
public class PointOfSaleServiceException extends RuntimeException {
    
    public PointOfSaleServiceException(String message) {
        super(message);
    }
    
    public PointOfSaleServiceException(Throwable cause) {
        super(cause);
    }

}
