package com.memegotchi.game.engine;

import com.memegotchi.game.model.PetModel;

public class PetEngine {
    public interface SleepListener {
        void onSleepStart();
        void onSleepEnd();
    }

    private PetModel pet;
    private float accumulatedTime = 0;
    private SleepListener sleepListener;

    private static final float GAME_TICK = 60f;
    private static final float ENERGY_REGEN_RATE = 1f;

    private float tickMultiplier = 1.0f;
    private float energyRegenAccumulator = 0f;

    public PetEngine(PetModel pet) {
        this.pet = pet;
    }

    public void setSleepListener(SleepListener listener) {
        this.sleepListener = listener;
    }

    public void setTickMultiplier(float multiplier) {
        this.tickMultiplier = multiplier;
    }

    public void updateOffline(long deltaMinutes) {
        if (deltaMinutes <= 0) return;
        long ticks = deltaMinutes * 60 / (long) GAME_TICK;
        for (long i = 0; i < ticks && i < 500; i++) {
            pet.setHunger(pet.getHunger() - 3);
            pet.setHappiness(pet.getHappiness() - 5);
            pet.setEnergy(pet.getEnergy() - 4);
            pet.setCleanliness(pet.getCleanliness() - 1);
        }
        if (pet.getHunger() <= 0 || pet.getHappiness() <= 0) {
            pet.setEnergy(Math.max(0, pet.getEnergy() - 10));
        }
    }

    public void update(float deltaSeconds) {
        if (pet.isSleeping()) {
            energyRegenAccumulator += ENERGY_REGEN_RATE * deltaSeconds;
            if (energyRegenAccumulator >= 1f) {
                int regen = (int) energyRegenAccumulator;
                pet.setEnergy(pet.getEnergy() + regen);
                energyRegenAccumulator -= regen;
            }
            if (pet.getEnergy() >= 100) {
                pet.setSleeping(false);
                if (sleepListener != null) sleepListener.onSleepEnd();
                energyRegenAccumulator = 0;
            }
        }

        accumulatedTime += deltaSeconds;
        if (accumulatedTime >= GAME_TICK * tickMultiplier) {
            accumulatedTime -= GAME_TICK * tickMultiplier;

            pet.setHunger(pet.getHunger() - 3);
            pet.setHappiness(pet.getHappiness() - 5);
            if (!pet.isSleeping()) {
                pet.setEnergy(pet.getEnergy() - 4);
            }
            pet.setCleanliness(pet.getCleanliness() - 1);

            if (pet.getHunger() <= 0 || pet.getHappiness() <= 0) {
                pet.setEnergy(Math.max(0, pet.getEnergy() - 10));
            }
        }
    }

    public void playFishing() {
        pet.setHappiness(Math.min(100, pet.getHappiness() + 1));
        pet.setEnergy(Math.max(0, pet.getEnergy() - 1));
        pet.setHunger(Math.max(0, pet.getHunger() - 2));
        pet.setCleanliness(Math.max(0, pet.getCleanliness() - 1));
    }

    public void feed() {
        pet.setHunger(Math.min(100, pet.getHunger() + 20));
        pet.setHappiness(Math.min(100, pet.getHappiness() + 5));
    }

    public void play() {
        pet.setHappiness(Math.min(100, pet.getHappiness() + 15));
        pet.setEnergy(Math.max(0, pet.getEnergy() - 5));
        pet.setHunger(Math.max(0, pet.getHunger() - 5));
    }

    public void clean() {
        pet.setCleanliness(100);
        pet.setHappiness(Math.min(100, pet.getHappiness() + 5));
    }

    public void sleep() {
        pet.setSleeping(true);
        if (sleepListener != null) sleepListener.onSleepStart();
    }

    public void wakeUp() {
        pet.setSleeping(false);
        if (sleepListener != null) sleepListener.onSleepEnd();
    }

    public boolean isSleeping() {
        return pet.isSleeping();
    }

    public PetModel getPet() {
        return pet;
    }
}
