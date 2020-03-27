package newapp.com.newshunter.Model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NewsList {


    @SerializedName("status")
    public String status;
    @SerializedName("totalResults")
    public Integer totalResults;
    @SerializedName("articles")
    public List<Datum> articles = new ArrayList();

    public class Datum {

        @SerializedName("source")
        public HashMap source;
        @SerializedName("author")
        public String author;
        @SerializedName("title")
        public String title;
        @SerializedName("description")
        public String description;
        @SerializedName("url")
        public String url;
        @SerializedName("urlToImage")
        public String urlToImage;
        @SerializedName("publishedAt")
        public String publishedAt;

    }
}