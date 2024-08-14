package com.scottparrillo.gamepulse;
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
        float conv = Float.valueOf(time);
        toReturn.setGameTime(conv);
        toReturn.setGamePlatform(platform);

        return toReturn;
    }
}
