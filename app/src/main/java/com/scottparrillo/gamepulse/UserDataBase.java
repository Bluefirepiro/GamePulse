package com.scottparrillo.gamepulse;
import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {UserDE.class}, version = 1)
public abstract class UserDataBase extends RoomDatabase {
    public abstract UserDao userDao();
}