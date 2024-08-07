package com.scottparrillo.gamepulse;

import java.util.ArrayList;

public class Game{
    boolean completed = false;
    boolean allAchiev = false;
    float gameTime = 0;
    float timeToBeat = 0;
    int gameId = 0;
    String gamePlatform = "";
    String coverURL = "";
    String gameName = "";
    String gameDescription = "";
    String gameGenre = "";
    String gameReleaseDate = "";

    //Games have multiple achievements so just adding a list so we can iter through them
    ArrayList<Achievement> achievements;


}
