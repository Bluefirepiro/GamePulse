package com.scottparrillo.gamepulse;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
//The goal of this database is to keep track of the users games and import them from the steam/xbox api
@Entity(tableName = "game_table")
public class GameDE {
    @PrimaryKey
    //game id
    public int game_id;
    //Creating the different columns for the database
    @ColumnInfo(name = "game_name")
    public String gameName;
    @ColumnInfo(name = "game_description")
    public String gameDescription;
    @ColumnInfo(name = "game_time")
    public float gameTime;
    @ColumnInfo(name = "total_achievements")
    public float totalAchievements;
    @ColumnInfo(name = "earned_achievements")
    public float earnedAchievements;
    @ColumnInfo(name = "game_cover")
    public String gameCover;
    @ColumnInfo(name = "game_completed")
    public boolean gameCompleted;
    @ColumnInfo(name = "game_fully_completed")
    public boolean gameFullyCompleted;
    @ColumnInfo(name = "time_to_beat")
    public boolean timeToBeat;
    @ColumnInfo(name = "game_platform")
    public String gamePlatform;
    @ColumnInfo(name = "cover_URL")
    public String coverURL;
    @ColumnInfo(name = "game_genre")
    public String gameGenre;
    @ColumnInfo(name = "release_date")
    public String releaseDate;

}
