package pos.service;

import java.math.BigDecimal;

import pos.model.Pricing;

/**
 * Defines contract for the application controller and service.
 */
public interface PointOfSaleService {

    /**
     * Create a new POS terminal in the system, and return its ID.
     */
    String activate();
    
    /**
     * Set pricing to the given POS terminal
     * 
     * @param terminalId Terminal ID
     * @param pricings   Zero or more pricing objects
     * @throws PointOfSaleServiceException in case when terminal is not found by ID
     */
    void setPricing(String terminalId, Pricing ... pricings) throws PointOfSaleServiceException;
    
    /**
     * Scan a product on the given POS terminal
     * 
     * @param terminalId Terminal ID
     * @param productCode Product code
     * @throws PointOfSaleServiceException in case when terminal is not found by ID
     */
    void scan(String terminalId, String productCode) throws PointOfSaleServiceException;
    
    /**
     * Calculate grand total on the given POS terminal.
     * Once this method is executed, the POS terminal is removed, and the any following methods
     * on it will result in exception.
     * 
     * @param terminalId Terminal ID
     * @return terminal's grand total
     * @throws PointOfSaleServiceException in case when terminal is not found by ID
     */
    BigDecimal calculateTotal(String terminalId) throws PointOfSaleServiceException;
    
}
