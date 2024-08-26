package com.scottparrillo.gamepulse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface SteamAchievementPercentagesAPI {
    @GET("ISteamUserStats/GetGlobalAchievementPercentagesForApp/v0002/?gameid={440}&format=json")
    Call<List<SteamAchievementPercentages>> getAchievementPercentages(@Path("id") int gameId);
}
