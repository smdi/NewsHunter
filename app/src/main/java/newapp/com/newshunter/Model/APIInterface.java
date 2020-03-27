package newapp.com.newshunter.Model;


import retrofit2.Call;

import retrofit2.http.GET;
import retrofit2.http.Query;

public interface APIInterface {

    @GET("/v2/top-headlines")
    Call<NewsList> getNewsList(@Query("country") String country,@Query("category") String category , @Query("apiKey") String apiKey);
}
