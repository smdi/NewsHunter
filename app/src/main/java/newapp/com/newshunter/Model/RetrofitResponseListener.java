package newapp.com.newshunter.Model;



public interface RetrofitResponseListener {

    void onFailure();

    void onSuccess(NewsList body);
}
