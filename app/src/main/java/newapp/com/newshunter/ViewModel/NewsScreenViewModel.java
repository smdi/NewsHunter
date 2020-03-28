package newapp.com.newshunter.ViewModel;

import android.content.Context;
import android.widget.Toast;

import androidx.lifecycle.ViewModel;
import newapp.com.newshunter.Model.APIInterface;
import newapp.com.newshunter.Model.ApiClient;
import newapp.com.newshunter.Model.NewsList;
import newapp.com.newshunter.Model.RetrofitResponseListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsScreenViewModel extends ViewModel {


    private Context application;
    private APIInterface apiInterface;
    private String country,preCountry = "1";
    private NewsList newsList;
    private String category, preCategory = "1";

    public  NewsScreenViewModel(Context application, String country, String  category){
        this.application = application;
        this.country = country;
        this.category = category;

    }

    private APIInterface getApiInterface(){
        if(apiInterface == null){
            apiInterface = ApiClient.getClient().create(APIInterface.class);
            return apiInterface;
        }
        else return apiInterface;
    }

    public void getNewsList(final RetrofitResponseListener retrofitResponseListener) {

        if(newsList == null | !(preCountry.equals(country)) | !(preCategory.equals(category))){

            preCountry = country;
            preCategory = category;

            Call<NewsList> call = getApiInterface().getNewsList(country,category,"67c6296f19c2443d861fd6a878dd3548");
            call.enqueue(new Callback<NewsList>() {
                @Override
                public void onResponse(Call<NewsList> call, Response<NewsList> response) {

                    if(response.isSuccessful()){
                        newsList = response.body();
                        retrofitResponseListener.onSuccess(newsList);
                    }
                    else {

                        retrofitResponseListener.onFailure();
                    }
                }

                @Override
                public void onFailure(Call<NewsList> call, Throwable t) {
                    call.cancel();
                    Toast.makeText(application,""+t,Toast.LENGTH_SHORT).show();
                }
            });

        }
        else {
            retrofitResponseListener.onSuccess(newsList);
        }
    }


}
