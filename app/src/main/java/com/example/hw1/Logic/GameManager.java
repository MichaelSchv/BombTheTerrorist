package com.example.hw1.Logic;

import java.util.Random;

public class GameManager {
    private final int COLS = 3;
    private final int ROWS = 5;
    private int life;
    private int misses;
    private Random rnd;



    public GameManager(int life)
    {
        this.life = life;
        rnd = new Random();
        misses=0;
    }

    public int getMisses() {
        return this.misses;
    }
    public void incrementMisses()
    {
        this.misses++;
    }
    public int getLife() {
        return this.life;
    }

    public boolean isEndlessMode()
    {
        return this.getMisses() >= this.getLife();
    }

    public int getRandomCol(final int numCols)
    {
        return rnd.nextInt(numCols);
    }

}
