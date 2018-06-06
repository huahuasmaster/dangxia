package dangxia.com.dto;

public class PriceSection {

    private double max;
    private double min;

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public PriceSection(double min, double max) {
        this.max = max;
        this.min = min;
    }

    @Override
    public String toString() {
        return "PriceSection{" +
                "max=" + max +
                ", min=" + min +
                '}';
    }
}
