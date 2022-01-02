import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

public class Mastermind {

    /*
     * ordering doesn't matter, these are the possible response peg combinations for
     * a given code
     */
    static String[] possibleResponses = { "CCCC", "CCCW", "CCCX", "CCWW", "CCWX", "CCXX", "CWWW", "CWWX", "CWXX",
            "CXXX",
            "WWWW", "WWWX", "WWXX", "WXXX", "XXXX" };

    static HashSet<String> possibleCodes;

    static HashSet<String> workingSet;

    static HashMap<Integer, String> colorMap;

    public static void main(String[] args) {
        initialize();

        Scanner reader = new Scanner(System.in);

        String currentResponse = "";

        String currentGuess = "1122";

        System.out.println("Let's pay Mastermind! Make your code, and no cheating please!");

        while (true) {
            possibleCodes.remove(currentGuess);
            System.out.println("I guess: " + translateNumberCode(currentGuess));
            System.out.println(
                    "Please input the response for this guess (Use 'C' for colored, 'W' for white, and 'X' for blank");

            currentResponse = reader.nextLine();
            if (currentResponse.equals("CCCC"))
                break;
            ArrayList<String> removeList = new ArrayList<String>();
            for (String s : workingSet) {
                String thisResponse = generateResponse(s, currentGuess);
                if (!thisResponse.equals(currentResponse)) {
                    removeList.add(s);
                }
            }
            for (String s : removeList)
                workingSet.remove(s);

            currentGuess = minimaxNextGuess();

        }
        System.out.println("I won! Good game!");

        reader.close();
    }

    static String minimaxNextGuess() {
        ArrayList<String>[] scoreArr = new ArrayList[1297];
        for (int i = 0; i < 1297; i++)
            scoreArr[i] = new ArrayList<>();

        for (String possibleGuess : possibleCodes) {
            int score = getMinimaxScore(possibleGuess);
            // System.out.printf("Found score of %d for possibleGuess %s\n", score,
            // possibleGuess);
            scoreArr[score].add(possibleGuess);
        }
        for (int i = 1296; i >= 0; i--) {
            if (scoreArr[i].size() > 0) {
                Collections.sort(scoreArr[i]);
                for (String s : scoreArr[i]) {
                    if (workingSet.contains(s))
                        return s;
                }
                return scoreArr[i].get(0);
            }
        }
        System.out.println("CRITICAL ERROR IN MINIMAX: CRASH IMMINENT");
        return null;
    }

    static int getMinimaxScore(String possibleGuess) {
        int maxHits = 0;
        for (String response : possibleResponses) {
            // System.out.printf("minimaxScore: starting with response: %s\n", response);
            int hits = 0;
            for (String s : workingSet) {
                String thisResponse = generateResponse(s, possibleGuess);
                // System.out.printf("generateResponse for %s, %s : %s\n", s, possibleGuess,
                // thisResponse);
                if (thisResponse.equals(response)) {
                    // System.out.printf("Hit! Response for %s, %s was %s\n", s, possibleGuess,
                    // thisResponse);
                    hits += 1;
                }
            }
            if (hits > maxHits)
                maxHits = hits;
        }
        // System.out.printf("maxHits: %d\n", maxHits);
        int score = workingSet.size() - maxHits;
        return score;
    }

    /*
     * given a secret code, and a guess at that code, generate the response
     */
    static String generateResponse(String guess, String code) {
        int oneCount = 0;
        int twoCount = 0;
        int threeCount = 0;
        int fourCount = 0;
        int fiveCount = 0;
        int sixCount = 0;
        String retString = "";
        ArrayList<Integer> correctIndices = new ArrayList<>();

        /*
         * first pass: find exactly correct pegs and setup "budgets"
         */
        for (int i = 0; i < 4; i++) {
            char guessChar = guess.charAt(i);
            char codeChar = code.charAt(i);
            if (guessChar == codeChar) {
                correctIndices.add(i);
                retString += "C";
                continue;
            } else {
                switch (codeChar) {
                    case '1':
                        oneCount += 1;
                        break;
                    case '2':
                        twoCount += 1;
                        break;
                    case '3':
                        threeCount += 1;
                        break;
                    case '4':
                        fourCount += 1;
                        break;
                    case '5':
                        fiveCount += 1;
                        break;
                    case '6':
                        sixCount += 1;
                        break;
                }
            }
        }

        /*
         * second pass: for non exactly correct pegs, determine if they earn a white peg
         * or no peg
         */
        for (int i = 0; i < 4; i++) {
            if (correctIndices.contains(i))
                continue;
            char guessChar = guess.charAt(i);
            switch (guessChar) {
                case '1':
                    if (oneCount > 0) {
                        retString += "W";
                        oneCount -= 1;
                    } else {
                        retString += "X";
                    }
                    break;
                case '2':
                    if (twoCount > 0) {
                        retString += "W";
                        twoCount -= 1;
                    } else {
                        retString += "X";
                    }
                    break;
                case '3':
                    if (threeCount > 0) {
                        retString += "W";
                        threeCount -= 1;
                    } else {
                        retString += "X";
                    }
                    break;
                case '4':
                    if (fourCount > 0) {
                        retString += "W";
                        fourCount -= 1;
                    } else {
                        retString += "X";
                    }
                    break;
                case '5':
                    if (fiveCount > 0) {
                        retString += "W";
                        fiveCount -= 1;
                    } else {
                        retString += "X";
                    }
                    break;
                case '6':
                    if (sixCount > 0) {
                        retString += "W";
                        sixCount -= 1;
                    } else {
                        retString += "X";
                    }
                    break;
            }
        }

        /*
         * sort the return string
         */
        char[] arr = retString.toCharArray();
        Arrays.sort(arr);
        retString = new String(arr);
        return retString;
    }

    /*
     * use colorMap to translate a number string into a human-readable one
     */
    static String translateNumberCode(String numberCode) {
        String retString = "";
        for (int i = 0; i < 4; i++) {
            int currInt = Integer.parseInt(numberCode.substring(i, i + 1));
            String colorString = colorMap.get(currInt);
            retString += (colorString + " ");
        }
        return retString;
    }

    /*
     * setup initial values
     */
    static void initialize() {
        possibleCodes = new HashSet<String>();
        colorMap = new HashMap<Integer, String>();
        colorMap.put(1, "Black");
        colorMap.put(2, "White");
        colorMap.put(3, "Red");
        colorMap.put(4, "Yellow");
        colorMap.put(5, "Green");
        colorMap.put(6, "Blue");
        for (int i = 1; i < 7; i++)
            for (int j = 1; j < 7; j++)
                for (int k = 1; k < 7; k++)
                    for (int l = 1; l < 7; l++)
                        possibleCodes.add(
                                Integer.toString(i) + Integer.toString(j) + Integer.toString(k) + Integer.toString(l));
        workingSet = (HashSet<String>) possibleCodes.clone();

    }
}