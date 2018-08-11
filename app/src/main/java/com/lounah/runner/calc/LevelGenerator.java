package com.lounah.runner.calc;

public class LevelGenerator {
    private int maxSpikes;
    private int minFreeSpace;
    private int levelWidth;
    private int spikeFreq;
    private boolean[] levelMap;

    private int spikesBefore = 0;
    private int freeSpaceBefore = 0;

    //5 - лучший вариант
    //больше 7 - не очень
    LevelGenerator(int maxSpikes, int minFree, int width, int frequency) {
            this.maxSpikes = maxSpikes;
            minFreeSpace = minFree - 1;
            levelWidth = width;
            levelMap = new boolean[width];
            spikeFreq = frequency;
    }

    boolean[] initLevel() {
        levelMap[0] = false;
        for (int i = 1; i < levelWidth; ++i) {
            levelMap[i] = normalizePoisson(getPoisson(spikeFreq), levelMap[i - 1]);
        }

        return levelMap;
    }

    void makeNextFameMap() {
        System.arraycopy(levelMap, 1, levelMap, 0, levelWidth - 1);
        levelMap[levelWidth - 1] = normalizePoisson(getPoisson(spikeFreq), levelMap[levelWidth - 2]);
    }

    private boolean normalizePoisson(boolean event, boolean eventBefore) {
        if (!eventBefore && freeSpaceBefore < minFreeSpace) {
            ++freeSpaceBefore;
            return false;
        }
        freeSpaceBefore = 0;
        if (spikesBefore < maxSpikes) {
            ++spikesBefore;
            return event;
        } else {
            spikesBefore = 0;
            return false;
        }
    }

    private boolean getPoisson(double lambda) {
        double L = Math.exp(-lambda);
        double p = 1.0;
        int k = 0;

        do {
            k++;
            p *= Math.random();
        } while (p > L);

        return (k - 1) > 5;
    }
}
