package com.google.cloud.android.speech;

import android.content.Context;
import android.content.Intent;

import java.util.Arrays;
import java.util.List;

public class Utils {

    public static void startNewActivity(Context context, Class activity) {
        Intent intent = new Intent(context, activity);
        context.startActivity(intent);
    }

    public static long wordsToNumber(String input) {
        boolean isValidInput = true;
        long result = 0;
        long finalResult = 0;
        List<String> allowedStrings = Arrays.asList
                (
                        "zero", "one", "two", "three", "four", "five", "six", "seven",
                        "eight", "nine", "ten", "eleven", "twelve", "thirteen", "fourteen",
                        "fifteen", "sixteen", "seventeen", "eighteen", "nineteen", "twenty",
                        "thirty", "forty", "fifty", "sixty", "seventy", "eighty", "ninety",
                        "hundred", "thousand", "million", "billion", "trillion"
                );



//        String input = "One hundred two thousand and thirty four";

        if (input != null && input.length() > 0) {
            input = input.replaceAll("-", " ");
            input = input.toLowerCase().replaceAll(" and", " ");
            String[] splittedParts = input.trim().split("\\s+");

            for (String str : splittedParts) {
                if (!allowedStrings.contains(str)) {
                    isValidInput = false;
                    System.out.println("Invalid word found : " + str);
                    break;
                }
            }
            if (isValidInput) {
                for (String str : splittedParts) {
                    if (str.equalsIgnoreCase("zero")) {
                        result += 0;
                    } else if (str.equalsIgnoreCase("one")) {
                        result += 1;
                    } else if (str.equalsIgnoreCase("two")) {
                        result += 2;
                    } else if (str.equalsIgnoreCase("three")) {
                        result += 3;
                    } else if (str.equalsIgnoreCase("four")) {
                        result += 4;
                    } else if (str.equalsIgnoreCase("five")) {
                        result += 5;
                    } else if (str.equalsIgnoreCase("six")) {
                        result += 6;
                    } else if (str.equalsIgnoreCase("seven")) {
                        result += 7;
                    } else if (str.equalsIgnoreCase("eight")) {
                        result += 8;
                    } else if (str.equalsIgnoreCase("nine")) {
                        result += 9;
                    } else if (str.equalsIgnoreCase("ten")) {
                        result += 10;
                    } else if (str.equalsIgnoreCase("eleven")) {
                        result += 11;
                    } else if (str.equalsIgnoreCase("twelve")) {
                        result += 12;
                    } else if (str.equalsIgnoreCase("thirteen")) {
                        result += 13;
                    } else if (str.equalsIgnoreCase("fourteen")) {
                        result += 14;
                    } else if (str.equalsIgnoreCase("fifteen")) {
                        result += 15;
                    } else if (str.equalsIgnoreCase("sixteen")) {
                        result += 16;
                    } else if (str.equalsIgnoreCase("seventeen")) {
                        result += 17;
                    } else if (str.equalsIgnoreCase("eighteen")) {
                        result += 18;
                    } else if (str.equalsIgnoreCase("nineteen")) {
                        result += 19;
                    } else if (str.equalsIgnoreCase("twenty")) {
                        result += 20;
                    } else if (str.equalsIgnoreCase("thirty")) {
                        result += 30;
                    } else if (str.equalsIgnoreCase("forty")) {
                        result += 40;
                    } else if (str.equalsIgnoreCase("fifty")) {
                        result += 50;
                    } else if (str.equalsIgnoreCase("sixty")) {
                        result += 60;
                    } else if (str.equalsIgnoreCase("seventy")) {
                        result += 70;
                    } else if (str.equalsIgnoreCase("eighty")) {
                        result += 80;
                    } else if (str.equalsIgnoreCase("ninety")) {
                        result += 90;
                    } else if (str.equalsIgnoreCase("hundred")) {
                        result *= 100;
                    } else if (str.equalsIgnoreCase("thousand")) {
                        result *= 1000;
                        finalResult += result;
                        result = 0;
                    } else if (str.equalsIgnoreCase("million")) {
                        result *= 1000000;
                        finalResult += result;
                        result = 0;
                    } else if (str.equalsIgnoreCase("billion")) {
                        result *= 1000000000;
                        finalResult += result;
                        result = 0;
                    } else if (str.equalsIgnoreCase("trillion")) {
                        result *= 1000000000000L;
                        finalResult += result;
                        result = 0;
                    }
                }

                finalResult += result;
                result = 0;

            }

        }

        return finalResult;


    }
    public static long sinhalaWordsToNumber(String input) {
        boolean isValidInput = true;
        long result = 0;
        long finalResult = 0;
        List<String> allowedStrings = Arrays.asList
                (
                        "බින්දුව", "එක", "දෙක", "තුන", "හතර", "පහ", "හය", "හත",
                        "අට", "නවය", "දහය", "එකොලහ", "දොලහ", "දහතුන", "දාහතර",
                        "පහලොව", "දහසය", "දාහත", "දහ අට", "දහ නවය", "විස්ස",
                        "තිහ", "හතලිහ", "පනහ", "හැට", "හැත්තෑව", "අසූව", "අනූව",
                        "සියය", "දාහ", "මිලියන", "බිලියන", "ට්රිලියනය"
                );



//        String input = "One hundred two thousand and thirty four";

        if (input != null && input.length() > 0) {
            input = input.replaceAll("-", " ");
            input = input.toLowerCase().replaceAll(" and", " ");
            String[] splittedParts = input.trim().split("\\s+");

            for (String str : splittedParts) {
                if (!allowedStrings.contains(str)) {
                    isValidInput = false;
                    System.out.println("Invalid word found : " + str);
                    break;
                }
            }
            if (isValidInput) {
                for (String str : splittedParts) {
                    if (str.equalsIgnoreCase("බින්දුව")) {
                        result += 0;
                    } else if (str.equalsIgnoreCase("එක")) {
                        result += 1;
                    } else if (str.equalsIgnoreCase("දෙක")) {
                        result += 2;
                    } else if (str.equalsIgnoreCase("තුන")) {
                        result += 3;
                    } else if (str.equalsIgnoreCase("හතර")) {
                        result += 4;
                    } else if (str.equalsIgnoreCase("පහ")) {
                        result += 5;
                    } else if (str.equalsIgnoreCase("හය")) {
                        result += 6;
                    } else if (str.equalsIgnoreCase("හත")) {
                        result += 7;
                    } else if (str.equalsIgnoreCase("අට")) {
                        result += 8;
                    } else if (str.equalsIgnoreCase("නවය")) {
                        result += 9;
                    } else if (str.equalsIgnoreCase("දහය")) {
                        result += 10;
                    } else if (str.equalsIgnoreCase("එකොලහ")) {
                        result += 11;
                    } else if (str.equalsIgnoreCase("දොලහ")) {
                        result += 12;
                    } else if (str.equalsIgnoreCase("දහතුන")) {
                        result += 13;
                    } else if (str.equalsIgnoreCase("දාහතර")) {
                        result += 14;
                    } else if (str.equalsIgnoreCase("පහලොව")) {
                        result += 15;
                    } else if (str.equalsIgnoreCase("දහසය")) {
                        result += 16;
                    } else if (str.equalsIgnoreCase("දාහත")) {
                        result += 17;
                    } else if (str.equalsIgnoreCase("දහ අට")) {
                        result += 18;
                    } else if (str.equalsIgnoreCase("දහ නවය")) {
                        result += 19;
                    } else if (str.equalsIgnoreCase("විස්ස")) {
                        result += 20;
                    } else if (str.equalsIgnoreCase("තිහ")) {
                        result += 30;
                    } else if (str.equalsIgnoreCase("හතලිහ")) {
                        result += 40;
                    } else if (str.equalsIgnoreCase("පනහ")) {
                        result += 50;
                    } else if (str.equalsIgnoreCase("හැට")) {
                        result += 60;
                    } else if (str.equalsIgnoreCase("හැත්තෑව")) {
                        result += 70;
                    } else if (str.equalsIgnoreCase("අසූව")) {
                        result += 80;
                    } else if (str.equalsIgnoreCase("අනූව")) {
                        result += 90;
                    } else if (str.equalsIgnoreCase("සියය")) {
                        result *= 100;
                    } else if (str.equalsIgnoreCase("දාහ")) {
                        result *= 1000;
                        finalResult += result;
                        result = 0;
                    } else if (str.equalsIgnoreCase("මිලියන")) {
                        result *= 1000000;
                        finalResult += result;
                        result = 0;
                    } else if (str.equalsIgnoreCase("බිලියන")) {
                        result *= 1000000000;
                        finalResult += result;
                        result = 0;
                    } else if (str.equalsIgnoreCase("ට්රිලියනය")) {
                        result *= 1000000000000L;
                        finalResult += result;
                        result = 0;
                    }
                }

                finalResult += result;
                result = 0;

            }

        }

        return finalResult;


    }
}
