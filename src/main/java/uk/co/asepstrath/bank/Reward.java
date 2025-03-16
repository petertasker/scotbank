package uk.co.asepstrath.bank;

import java.math.BigDecimal;

public class Reward {
    private final String name;
    private final String description;
    private final BigDecimal value;
    private final double chance;

    public Reward(String name, String description, BigDecimal value, double chance) {
        this.name = name;
        this.description = description;
        this.value = value;
        this.chance = chance;
    }

    public String getRewardsName() {
        return this.name;
    }

    public String getRewardsDescription() {
        return this.description;
    }

    public BigDecimal getRewardsValue() {
        return this.value;
    }

    public double getRewardsChance() {
        return this.chance;
    }

    @Override
    public String toString() {
        return String.format("name: %s%ndescription: %s%nvalue: %s%n %s%nchance: %s%n",
                getRewardsName(), getRewardsDescription(), getRewardsValue(), getRewardsChance());
    }

}
