package com.scottparrillo.gamepulse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

//This class is going to contain helper methods for converting game inputs and outputs
public final class GameUtil {



    //Takes in string inputs and returns a game database object
    public static Game InputToGame(String name, String gameDesc, String time, String date, String platform )

    {
        Game toReturn = new Game();
        //Just set var from inputs
        toReturn.setGameName(name);
        toReturn.setGameDescription(gameDesc);
        //Quick conversion from string to float
        float conv = Float.parseFloat(time);
        toReturn.setGameTime(conv);
        toReturn.setGamePlatform(platform);

        return toReturn;
    }
    private static float parseTimeString(String time) {
        // Regex to find the first sequence of digits, optionally followed by a decimal point and more digits
        Pattern pattern = Pattern.compile("\\d+(\\.\\d+)?");
        Matcher matcher = pattern.matcher(time);

        if (matcher.find()) {
            try {
                return Float.parseFloat(matcher.group());
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return 0f; // Default value if the number cannot be parsed
            }
        } else {
            return 0f; // Default value if no numeric part is found
        }
    }
}
