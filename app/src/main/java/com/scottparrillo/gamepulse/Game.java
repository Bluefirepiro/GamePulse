package com.scottparrillo.gamepulse;

import java.util.ArrayList;

public class Game{
    boolean completed = false;
    boolean allAchiev = false;
    //Using the time class seems like too much if we are just keeping track of total time
    float gameTime = 0;
    String gameName = "";
    //Games have multiple achievements so just adding a list so we can iter through them
    ArrayList<Achievement> achievements;


}
