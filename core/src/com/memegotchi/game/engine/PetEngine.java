package com.memegotchi.game.engine;

import com.memegotchi.game.model.PetModel;

public class PetEngine {
    private PetModel pet;
    private float accumulatedTime = 0;

    public PetEngine(PetModel pet) {
        this.pet = pet;
    }

    public void update(long deltaMinutes) {
        if (deltaMinutes <= 0) return;

        pet.setHunger(pet.getHunger() - (int)(deltaMinutes / 30));
        pet.setHappiness(pet.getHappiness() - (int)(deltaMinutes / 45));
        pet.setEnergy(pet.getEnergy() - (int)(deltaMinutes / 60));
        pet.setCleanliness(pet.getCleanliness() - (int)(deltaMinutes / 90));

        // Если голод или счастье упали до 0 - штраф
        if (pet.getHunger() <= 0 || pet.getHappiness() <= 0) {
            pet.setEnergy(Math.max(0, pet.getEnergy() - 10));
        }
    }

    public void update(float deltaMinutes) {
        accumulatedTime += deltaMinutes;
        if (accumulatedTime >= 1.0f) {
            long minutes = (long) accumulatedTime;
            update(minutes);
            accumulatedTime -= minutes;
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