package pos.test;

import static pos.model.Pricing.*;

import java.math.BigDecimal;

import pos.model.PointOfSaleTerminal;
import pos.model.Pricing;

/**
 * Different test utility functions.
 */
public final class TestHelper {
    
    private TestHelper(){}
    
    public static void scan(PointOfSaleTerminal terminal, String s) {
        for (int i = 0; i < s.length(); i++) {
            terminal.scan(s.charAt(i) + "");
        }
    }
    
    public static Pricing[] getTestPricing() {
        return new Pricing[] {
                perUnitPricing("A", new BigDecimal(1.25)),
                volumePricing("A", 3, new BigDecimal(3)),
                perUnitPricing("B", new BigDecimal(4.25)),
                perUnitPricing("C", new BigDecimal(1)),
                volumePricing("C", 6, new BigDecimal(5)),
                perUnitPricing("D", new BigDecimal(0.75)),
        };
    }

}
