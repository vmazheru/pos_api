package pos.controller;

import static org.junit.jupiter.api.Assertions.*;
import static pos.test.TestHelper.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import pos.client.PointOfSaleTerminalClient;
import pos.client.PointOfSaleTerminalClientException;
import pos.model.PointOfSaleTerminal;
import pos.model.Pricing;
import pos.test.TestConfiguration;

/**
 * Test controller methods.  This test should not test any "business" logic, but mainly verify
 * that controller methods work properly.
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class PointOfSaleControllerTest {
    
    private PointOfSaleTerminal terminal;
    
    @BeforeEach
    public void before() {
        terminal = new PointOfSaleTerminalClient();
    }

    @Test
    public void newTerminalHasId() {
        assertNotNull(terminal.getId());
    }
    
    @Test
    public void setPricingWorks() {
        assertDoesNotThrow(() -> terminal.setPricing(getTestPricing()));
        assertDoesNotThrow(() -> terminal.setPricing(new Pricing[0]));
    }
    
    @Test
    public void setNullPricingThrows() {
        assertThrows(PointOfSaleTerminalClientException.class, () -> terminal.setPricing((Pricing[])null));
    }
    
    @Test
    public void scanWorks() {
        terminal.setPricing(getTestPricing());
        assertDoesNotThrow(() -> scan(terminal, "ABCDABA"));
    }
    
    @Test
    public void scanThrowsOnUnknownProduct() {
        terminal.setPricing(getTestPricing());
        assertThrows(PointOfSaleTerminalClientException.class, () -> scan(terminal, "XYZ"));
    }
    
    @Test
    public void calculateTotalWorks() {
        terminal.setPricing(getTestPricing());
        scan(terminal, "ABCDABA");
        assertDoesNotThrow(() -> { 
            assertTrue(terminal.calculateTotal().doubleValue() > 0);
        });
    }
    
    @Test
    public void calculateTotalOnEmptyCartWorks() {
        assertDoesNotThrow(() -> { 
            assertTrue(terminal.calculateTotal().equals(new BigDecimal("0.00")));
        });
    }
    
    @Test
    public void calculateTotalRemovesTerminal() {
        terminal.calculateTotal().equals(new BigDecimal(0));
        assertThrows(PointOfSaleTerminalClientException.class, () -> terminal.setPricing(getTestPricing()));
    }
    
}
