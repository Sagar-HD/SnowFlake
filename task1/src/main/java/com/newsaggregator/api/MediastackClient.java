package com.newsaggregator.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.newsaggregator.model.NewsArticle;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.util.ArrayList;
import java.util.List;

public class MediastackClient implements NewsApiClient {
    private static final String BASE_URL = "https://api.mediastack.com/v1/news";
    private final OkHttpClient client;

    public MediastackClient() {
        this.client = new OkHttpClient();
    }

    @Override
    public String getApiName() {
        return "Mediastack";
    }

    @Override
    public List<NewsArticle> fetchTopNews(String apiKey) throws Exception {
        String apiUrl = BASE_URL + "?access_key=" + apiKey + "&languages=en&limit=10";
        Request request = new Request.Builder()
                .url(apiUrl)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new Exception("Failed to fetch news: " + response.code());
            }

            String responseBody = response.body().string();
            JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
            
            List<NewsArticle> articles = new ArrayList<>();
            if (jsonResponse.has("data")) {
                JsonArray dataArray = jsonResponse.getAsJsonArray("data");
                for (int i = 0; i < Math.min(10, dataArray.size()); i++) {
                    JsonObject articleObj = dataArray.get(i).getAsJsonObject();
                    
                    String source = articleObj.has("author") ? articleObj.get("author").getAsString() : "Unknown";
                    String title = articleObj.has("title") ? articleObj.get("title").getAsString() : "No title";
                    String description = articleObj.has("description") ? articleObj.get("description").getAsString() : "";
                    String url = articleObj.has("url") ? articleObj.get("url").getAsString() : "";
                    String publishedAt = articleObj.has("published_at") ? articleObj.get("published_at").getAsString() : "";
                    String author = articleObj.has("author") ? articleObj.get("author").getAsString() : "";
                    
                    articles.add(new NewsArticle(source, title, description, url, publishedAt, author));
                }
            }
            return articles;
        }
    }
}
