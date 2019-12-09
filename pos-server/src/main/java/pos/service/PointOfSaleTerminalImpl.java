package pos.service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

import pos.model.PointOfSaleTerminal;
import pos.model.PointOfSaleTerminalException;
import pos.model.Pricing;

/**
 * Implementation of {@code PointOfSaleTerminal} interface, which actully implements is functionality.
 */
final class PointOfSaleTerminalImpl implements PointOfSaleTerminal {
    
    // we keep Pricing objects sorted by count in descending order
    private static final Comparator<Pricing> reversedCountComparator =
            Comparator.comparing(Pricing::getCount).reversed();
    
    // POS terminal ID
    private final String id;
    
    // Pricing map. Keys are product codes, and values are
    // sets of Pricing objects sorted by count in descending order
    private final Map<String, SortedSet<Pricing>> pricingMap;
    
    // A map which contains scanned products. 
    // Keys are product codes, and the values are counts (how many times the product has been scanned)
    private final Map<String, Integer> shoppingCart;
    
    /**
     * Create a new POS terminal object.  The object will obtain a unique ID
     */
    public PointOfSaleTerminalImpl() {
        id = UUID.randomUUID().toString().replaceAll("-", "");
        pricingMap = new HashMap<>();
        shoppingCart = new HashMap<>();
    }
    
    @Override
    public String getId() {
        return id;
    }

    /**
     * Add the given {@code Pricing} objects.
     */
    @Override
    public void setPricing(Pricing... pricings) {
        for (Pricing p : pricings) {
            SortedSet<Pricing> s = pricingMap.get(p.getProductCode());
            if (s == null) {
                s = new TreeSet<>(reversedCountComparator);
                pricingMap.put(p.getProductCode(), s);
            }
            s.add(p);
        }
    }

    /**
     * Add the given product to the shopping cart.
     */
    @Override
    public void scan(String productCode) {
        if (!pricingMap.containsKey(productCode)) {
            throw new PointOfSaleTerminalException("No product found by code: " + productCode);
        }
        
        shoppingCart.compute(productCode, (key, oldVal) -> 
            (oldVal == null) ? new Integer(1) : new Integer(oldVal.intValue() + 1));
    }

    /**
     * Calculate the shopping cart total in a way that pricing with larger counts is applied first,
     * which results in the lower total.
     */
    @Override
    public BigDecimal calculateTotal() {
        BigDecimal total = new BigDecimal(0);
        for (Map.Entry<String, Integer> e : shoppingCart.entrySet()) {
            int count = e.getValue();
            for (Pricing p : pricingMap.get(e.getKey())) {
                if (count == 0) break;
                while (p.getCount() <= count) {
                    total = total.add(p.getPrice());
                    count -= p.getCount();
                }
            }
        }
        return total.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

}
