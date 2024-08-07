package com.scottparrillo.gamepulse;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {GameDE.class}, version = 1)
public abstract class GameDataBase extends RoomDatabase {
    public abstract GameDao gameDao();
}

