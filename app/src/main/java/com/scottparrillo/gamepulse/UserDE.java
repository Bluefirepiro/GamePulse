package com.scottparrillo.gamepulse;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_table")
public class UserDE {
    @PrimaryKey
    public int user_id;
//These are the different columns in the sqlite database
    @ColumnInfo(name = "user_name")
    public String userName;
    @ColumnInfo(name = "user_email")
    public String userEmail;
    @ColumnInfo(name = "pass_hash")
    public int passHash;
    @ColumnInfo(name = "creation_date")
    public String creationDate;
    @ColumnInfo(name = "last_login")
    public String lastLogin;
    @ColumnInfo(name = "avatar_url")
    public String avatarURL;
    @ColumnInfo(name = "bio")
    public String bio;
    @ColumnInfo(name = "total_play_time")
    public String totalPlayTime;
    @ColumnInfo(name = "total_achievements")
    public String totalAchievements;
    //Think these should all be string but unsure ask scott later or scott just msg me when you see this
    @ColumnInfo(name = "social_sharing")
    public String socialSharing;
    @ColumnInfo(name = "sharable_links")
    public String sharableLinks;
    @ColumnInfo(name = "shared_activities")
    public String sharedActivities;

}
