package com.scottparrillo.gamepulse;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

//The code below defines the methods of the database
@Dao
public interface GameDao {
    @Insert
    void insert(GameDE game);

    @Update
    void update(GameDE game);

    @Delete
    void delete(GameDE game);

    @Query("SELECT * FROM game_table WHERE game_Name = :gameName")
    GameDE getGameByName(String gameName);

    @Query("SELECT * FROM game_table")
    List<GameDE> getAll();


}
