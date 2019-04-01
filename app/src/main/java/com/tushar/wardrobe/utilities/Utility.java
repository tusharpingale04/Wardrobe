package com.tushar.wardrobe.utilities;

import java.util.Random;

public class Utility {

    public static int getRandomNumberFrom(int min, int max) {
        Random foo = new Random();
        return foo.nextInt((max + 1) - min) + min;
    }
}
