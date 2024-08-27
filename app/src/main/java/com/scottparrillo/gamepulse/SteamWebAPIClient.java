package com.scottparrillo.gamepulse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface SteamWebAPIClient {
    @GET("/ISteamUserStats/GetGlobalAchievementPercentagesForApp/v0002/?gameid={gameId}&format=json")
    Call<List<SteamAchievementPercentages>> getAchievementPercentages(@Path("gameId")
                                                                      int gameId);
}
