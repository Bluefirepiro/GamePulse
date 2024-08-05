package com.scottparrillo.gamepulse;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
//The goal of this database is to keep track of the users games and import them from the steam/xbox api
@Entity(tableName = "game_table")
public class GameDE {
    @PrimaryKey
    //game id
    int game_id;
    //Creating the different columns for the database
    @ColumnInfo(name = "game_name")
    String gameName;
    @ColumnInfo(name = "game_description")
    String gameDescription;
    @ColumnInfo(name = "game_time")
    float gameTime;
    @ColumnInfo(name = "total_achievements")
    float totalAchievements;
    @ColumnInfo(name = "earned_achievements")
    float earnedAchievements;
    @ColumnInfo(name = "game_cover")
    String gameCover;
    @ColumnInfo(name = "game_completed")
    boolean gameCompleted;
    @ColumnInfo(name = "game_fully_completed")
    boolean gameFullyCompleted;
    @ColumnInfo(name = "time_to_beat")
    boolean timeToBeat;
    @ColumnInfo(name = "game_platform")
    String gamePlatform;
    @ColumnInfo(name = "cover_URL")
    String coverURL;
    @ColumnInfo(name = "game_genre")
    String gameGenre;
    @ColumnInfo(name = "release_date")
    String releaseDate;

}
