package com.memegotchi.game.engine;

import com.badlogic.gdx.math.MathUtils;
import com.memegotchi.game.model.PetModel;

public class PetEngine {
    private PetModel pet;
    private float accumulatedTime = 0;

    private static final float GAME_TICK = 15f;

    public PetEngine(PetModel pet) {
        this.pet = pet;
    }

    public void updateOffline(long deltaMinutes) {
        if (deltaMinutes <= 0) return;
        long ticks = deltaMinutes * 60 / (long) GAME_TICK;
        for (long i = 0; i < ticks && i < 500; i++) {
            pet.setHunger(pet.getHunger() - MathUtils.random(2, 5));
            pet.setHappiness(pet.getHappiness() - MathUtils.random(1, 3));
            pet.setEnergy(pet.getEnergy() - MathUtils.random(1, 2));
            pet.setCleanliness(pet.getCleanliness() - MathUtils.random(1, 3));
        }
        if (pet.getHunger() <= 0 || pet.getHappiness() <= 0) {
            pet.setEnergy(Math.max(0, pet.getEnergy() - 10));
        }
    }

    public void update(float deltaSeconds) {
        accumulatedTime += deltaSeconds;
        if (accumulatedTime >= GAME_TICK) {
            accumulatedTime -= GAME_TICK;

            pet.setHunger(pet.getHunger() - MathUtils.random(2, 5));
            pet.setHappiness(pet.getHappiness() - MathUtils.random(1, 3));
            pet.setEnergy(pet.getEnergy() - MathUtils.random(1, 2));
            pet.setCleanliness(pet.getCleanliness() - MathUtils.random(1, 3));

            if (pet.getHunger() <= 0 || pet.getHappiness() <= 0) {
                pet.setEnergy(Math.max(0, pet.getEnergy() - 10));
            }
        }
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
        pet.setEnergy(100);
    }

    public PetModel getPet() {
        return pet;
    }
}
