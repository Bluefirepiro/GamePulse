package com.scottparrillo.gamepulse;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface UserDao {
    //This is where all the methods for the database go
    @Query("SELECT * FROM user_table")
    List<UserDE> getAll();
    @Insert
    void insert(UserDE game);

    @Update
    void update(UserDE game);

    @Delete
    void delete(UserDE game);

    @Query("SELECT * FROM user_table WHERE user_name = :userName")
    UserDE getUserByName(String userName);

}
