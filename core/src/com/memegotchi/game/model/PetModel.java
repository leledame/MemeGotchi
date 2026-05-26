package com.memegotchi.game.model;

import java.util.ArrayList;
import java.util.List;

public class PetModel {
    private int hunger = 100;
    private int happiness = 100;
    private int energy = 100;
    private int cleanliness = 100;
    private int coins = 100;
    private long lastUpdateTime = System.currentTimeMillis();
    private List<String> fishInventory = new ArrayList<>();
    private int shampooCount = 0;

    public int getHunger() { return hunger; }
    public int getHappiness() { return happiness; }
    public int getEnergy() { return energy; }
    public int getCleanliness() { return cleanliness; }
    public int getCoins() { return coins; }
    public long getLastUpdateTime() { return lastUpdateTime; }
    public List<String> getFishInventory() { return fishInventory; }
    public int getShampooCount() { return shampooCount; }

    public void setHunger(int hunger) { this.hunger = Math.max(0, Math.min(100, hunger)); }
    public void setHappiness(int happiness) { this.happiness = Math.max(0, Math.min(100, happiness)); }
    public void setEnergy(int energy) { this.energy = Math.max(0, Math.min(100, energy)); }
    public void setCleanliness(int cleanliness) { this.cleanliness = Math.max(0, Math.min(100, cleanliness)); }
    public void setCoins(int coins) { this.coins = Math.max(0, coins); }
    public void setLastUpdateTime(long time) { this.lastUpdateTime = time; }
    public void setFishInventory(List<String> inventory) { this.fishInventory = inventory; }
    public void setShampooCount(int count) { this.shampooCount = Math.max(0, count); }

    public void addFish(String fishName) {
        fishInventory.add(fishName);
    }

    public String takeTopFish() {
        if (fishInventory.isEmpty()) return null;
        return fishInventory.remove(fishInventory.size() - 1);
    }

    public boolean hasFish() {
        return !fishInventory.isEmpty();
    }

    public void addShampoo() {
        shampooCount++;
    }

    public boolean useShampoo() {
        if (shampooCount <= 0) return false;
        shampooCount--;
        return true;
    }

    public boolean hasShampoo() {
        return shampooCount > 0;
    }
}