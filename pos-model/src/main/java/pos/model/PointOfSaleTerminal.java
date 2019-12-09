package pos.model;

import java.math.BigDecimal;

/**
 * Describes the POS terminal contract, implemented by the client terminal and server implementation. 
 */
public interface PointOfSaleTerminal {

    /**
     * A terminal has unique ID.
     */
    String getId();
    
    /**
     * Add zero or more {@link Pricing} objects to the terminal.
     */
    void setPricing(Pricing ... pricings);

    /**
     * Scan a single product code.  Scanning a product will add on piece of it to the shopping cart.
     * 
     * @param productCode  The product code
     * @throws PointOfSaleTerminalException when the product code is not found in pricing (there is
     * no pricing info for this product code)
     */
    void scan(String productCode) throws PointOfSaleTerminalException;
    
    /**
     * Calculate the shopping cart's grand total.  If the shopping cart is empty, the method
     * return 0.00
     */
    BigDecimal calculateTotal();
    
}
