package com.arishi.AXAM.util;

import com.arishi.AXAM.exception.BadRequestException;

import java.util.HashSet;
import java.util.Set;

public class QuestionHelper {

    public static Boolean isOptionsUnique(String optionA, String optionB, String optionC, String optionD) {

        Set<String> options = new HashSet<>();

        options.add(optionA.trim().toLowerCase());
        options.add(optionB.trim().toLowerCase());
        options.add(optionC.trim().toLowerCase());
        options.add(optionD.trim().toLowerCase());

        return options.size() == 4;


    }
}
