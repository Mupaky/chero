package com.supplyboost.chero.utils;


import java.util.Random;

public class Generator {

    public static String generateNickName(){
        Random random = new Random();
        int randomNumber = 10000000 + random.nextInt(90000000);
        return "Chero" + randomNumber;
    }
}
