package com.lounah.runner.calc;

import java.util.Random;

public class LevelGenerator {
    private int maxSpikes = 2;
    private int minFreeSpace = 1;
    private int levelWidth;
    private int levelHeight;
    private int spikeFreqBorder = 5;
    private int starFreqBorder = 7;
    private char[][] levelMap;

    private int freeTopSpaceBefore = 0;
    private int topSpikesBefore = 0;
    private int freeBottomSpaceBefore = 0;
    private int bottomSpikesBefore = 0;

    private int score = 0;

    //5 - лучший вариант
    //больше 7 - не очень
    LevelGenerator(int width, int height) {
        levelWidth = width;
        levelHeight = height;
        levelMap = new char[height][width];
    }

    void setMedium() {
        maxSpikes = 5;
        minFreeSpace = 0;
    }

    void setHard() {
        maxSpikes = 5;
        minFreeSpace = 0;
        starFreqBorder = 5;
    }

    void setSpikeFreqBorder(int border) {
        spikeFreqBorder = border;
    }

    void setStarFreqBorder(int border) {
        starFreqBorder = border;
    }

    void setMinFreeSpace(int min) {
        minFreeSpace = min;
    }

    void setMaxSpikes(int max) {
        maxSpikes = max;
    }

    char[][] initLevel() {
        ++score;
        for (int i = 0; i < levelHeight; ++i) {
            levelMap[i][0] = 'e';
        }

        for (int i = 0; i < 10; ++i) {
            levelMap[levelHeight - 1][i] = 'e';
        }

        for (int i = 10; i < levelWidth; ++i) {
            setBottomItem(levelHeight - 1, i,
                    levelMap[levelHeight - 1][i - 1], getPoisson(spikeFreqBorder));
        }

        for (int i = 1; i < levelWidth; ++i) {
            setTopItem(0, i, levelMap[0][i - 1], getPoisson(spikeFreqBorder));
        }
        //genMiddle
        for (int level = 1; level < levelHeight - 1; ++level) {
            for (int pos = 1; pos < levelWidth; ++pos) {
                levelMap[level][pos] = 'e';
            }
        }
        return levelMap;
    }

    private void setTopItem(int line, int row, char eventBefore, boolean event) {
        if (event) {
            if (eventBefore == 'e' && freeTopSpaceBefore < minFreeSpace) {
                ++freeTopSpaceBefore;
                levelMap[line][row] = 'e';
            }
            freeTopSpaceBefore = 0;
            if (topSpikesBefore < maxSpikes) {
                ++topSpikesBefore;
                levelMap[line][row] = 't';
            } else {
                topSpikesBefore = 0;
                levelMap[line][row] = 'e';
            }
        } else {
            levelMap[line][row] = 'e';
        }
    }

    private void setBottomItem(int line, int row, char eventBefore, boolean event) {
        if (event) {
            if (eventBefore == 'e' && freeBottomSpaceBefore < minFreeSpace) {
                ++freeBottomSpaceBefore;
                levelMap[line][row] = 'e';
            }
            freeBottomSpaceBefore = 0;
            if (bottomSpikesBefore < maxSpikes) {
                ++bottomSpikesBefore;
                levelMap[line][row] = 'b';
            } else {
                bottomSpikesBefore = 0;
                levelMap[line][row] = 'e';
            }
        } else {
            levelMap[line][row] = 'e';
        }
    }

    private Random random = new Random();

    private void setMiddleItem(int row) {
        for (int i = 1; i < levelHeight - 2; ++i) {
            levelMap[i][row] = 'e';
        }

        boolean poisson = getPoisson(starFreqBorder);
        if (poisson) {
            int line = 1;
            int position = levelHeight - 3 + 1;
            if (position > 1) {
                line = random.nextInt(position);
            }
            levelMap[line][row] = 's';
        }
    }

    void makeNextFameMap() {
        ++score;
        for (int i = 0; i < levelHeight; ++i) {
            System.arraycopy(levelMap[i], 1, levelMap[i], 0, levelWidth - 1);
        }

        int last = levelWidth - 1;
        setBottomItem(levelHeight - 1, last,
                levelMap[levelHeight - 1][last], getPoisson(spikeFreqBorder));
        setTopItem(0, levelWidth - 1, levelMap[0][last], getPoisson(spikeFreqBorder));

        setMiddleItem(last);

        if (score % 1000 == 0) {
            int middle = levelHeight / 2;
            levelMap[middle][last] = 'l';
            score = 0;
        }
    }

    private double lambda = 4.0;

    private boolean getPoisson(int border) {
        double L = Math.exp(-lambda);
        double p = 1.0;
        int k = 0;

        do {
            k++;
            p *= Math.random();
        } while (p > L);

        return (k - 1) > border;
    }

    public static int getBinomial(int n, double p) {
        int x = 0;
        for (int i = 0; i < n; i++) {
            if (Math.random() < p)
                x++;
        }
        return x;
    }

//    private int spikesBefore = 0;
//    private int freeSpaceBefore = 0;

//    char getItem(char eventBefore, char event, int freeSpaceBefore, int spikesBefore) {
//        if (eventBefore == 'e' && freeTopSpaceBefore < minFreeSpace) {
//            ++freeTopSpaceBefore;
////            'e'
//        }
//        freeTopSpaceBefore = 0;
//        if (topSpikesBefore < maxSpikes) {
//            ++topSpikesBefore;
////            't'
//        } else {
//            topSpikesBefore = 0;
////            'e'
//        }
//        return 'e';
//    }

//    void setItem(int line, int row, char filler) {
//        boolean before = false;
//        if (levelMap[line][row - 1] != 'e') before = true;
//        if (normalizePoisson(getPoisson(spikeFreq), before)) {
//            levelMap[line][row] = filler;
//        } else {
//            levelMap[line][row] = 'e';
//        }
//    }
}