package pos.service;

import static org.junit.jupiter.api.Assertions.*;
import static pos.test.TestHelper.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import pos.model.PointOfSaleTerminal;
import pos.model.Pricing;

/**
 * Test POS terminal functionality. Tests here are unit tests, which verify POS terminal
 * business logic.
 */
public class PointOfSaleTerminalTest {

    @Test
    public void terminalHasId() {
        assertNotNull(getTerminal().getId());
    }
    
    @Test
    public void calculateTotal() {
        for (Map.Entry<String, BigDecimal> e : testShoppingCarts().entrySet()) {
            PointOfSaleTerminal terminal = getTerminal();
            terminal.setPricing(getTestPricing());
            scan(terminal, e.getKey());
            BigDecimal total = terminal.calculateTotal();
            assertEquals(e.getValue(), total);
        }
    }
    
    @Test
    public void calculateMoreComplexPricing() {
        PointOfSaleTerminal terminal = getTerminal();
        terminal.setPricing(new Pricing[] {
                Pricing.perUnitPricing("Z", new BigDecimal("2.00")),
                Pricing.volumePricing("Z", 3, new BigDecimal("5.00")),
                Pricing.volumePricing("Z", 10, new BigDecimal("10.00"))
        });
        
        String scan = "ZZZZZZZZZZZZZZZZZZ"; // 10 + 3 + 3 + 1 + 1 = 18
        assertEquals(18, scan.length());
        scan(terminal, scan); 
        
        BigDecimal total = terminal.calculateTotal(); // 10 + 5 + 5 + 2 + 2 = 24
        assertEquals(new BigDecimal("24.00"), total);
    }
    
    private static PointOfSaleTerminal getTerminal() {
        return new PointOfSaleTerminalImpl();
    }
    
    private static Map<String, BigDecimal> testShoppingCarts() {
        Map<String, BigDecimal> shoppingCarts = new HashMap<>();
        shoppingCarts.put("ABCDABA", new BigDecimal("13.25"));
        shoppingCarts.put("CCCCCCC", new BigDecimal("6.00"));
        shoppingCarts.put("ABCD",    new BigDecimal("7.25"));
        return shoppingCarts;
    }
}
