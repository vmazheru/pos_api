package pos.model;

import java.math.BigDecimal;

/**
 * This class represents a Volume Pricing object. Note that there is no separate class to 
 * represent Per Unit Pricing, because it may be represented as Volume Pricing with count 1.
 */
public final class Pricing {
    
    private final String productCode;
    private final Integer count;
    private final BigDecimal price;
    
    // necessary for JSON parsing
    @SuppressWarnings("unused")
    private Pricing() {
        this(null, null, null);
    }
    
    public Pricing(String productCode, Integer count, BigDecimal price) {
        this.productCode = productCode;
        this.count = count;
        this.price = price; 
    }

    public String getProductCode() {
        return productCode;
    }

    public Integer getCount() {
        return count;
    }

    public BigDecimal getPrice() {
        return price;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Pricing other = (Pricing) obj;
        return productCode.equals(other.productCode) &&
               count.equals(other.count) &&
               price.equals(other.price);
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + count.hashCode();
        result = prime * result + price.hashCode();
        result = prime * result + productCode.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Pricing [productCode=" + productCode + ", count=" + count + ", price=" + price + "]";
    }

    /**
     * Convenience method to return per unit pricing
     */
    public static Pricing perUnitPricing(String productCode, BigDecimal price) {
        return new Pricing(productCode, 1, price);
    }

    /**
     * Convenience method to return volume pricing
     */
    public static Pricing volumePricing(String productCode, int count, BigDecimal price) {
        return new Pricing(productCode, count, price);
    }

}
