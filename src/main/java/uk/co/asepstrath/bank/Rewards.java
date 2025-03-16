package uk.co.asepstrath.bank;

import java.math.BigDecimal;

public class Rewards {
    private String name;
    private String description;
    private BigDecimal value;
    private double chance;

    public Rewards(String name, String description, BigDecimal value, double chance){
        this.name = name;
        this.description = description;
        this.value = value;
        this.chance = chance;
    }
    public String getRewardsName(){
        return this.name;
    }
    public String getRewardsDescription(){
        return this.description;
    }
    public BigDecimal getRewardsValue(){
        return this.value;
    }
    public double getRewardsChance(){
        return this.chance;
    }
    @Override
    public String toString() {
        return String.format("name: %s%ndescription: %s%nvalue: %s%n %s%nchance: %s%n",
                getRewardsName(), getRewardsDescription(), getRewardsValue(),getRewardsChance());
    }

}
