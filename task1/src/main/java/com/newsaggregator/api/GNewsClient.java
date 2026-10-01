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

public class GNewsClient implements NewsApiClient {
    private static final String BASE_URL = "https://gnews.io/api/v4/top-headlines";
    private final OkHttpClient client;

    public GNewsClient() {
        this.client = new OkHttpClient();
    }

    @Override
    public String getApiName() {
        return "GNews";
    }

    @Override
    public List<NewsArticle> fetchTopNews(String apiKey) throws Exception {
        String apiUrl = BASE_URL + "?category=general&lang=en&max=10&apikey=" + apiKey;
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
            if (jsonResponse.has("articles")) {
                JsonArray articlesArray = jsonResponse.getAsJsonArray("articles");
                for (int i = 0; i < Math.min(10, articlesArray.size()); i++) {
                    JsonObject articleObj = articlesArray.get(i).getAsJsonObject();
                    
                    String source = articleObj.has("source") && articleObj.getAsJsonObject("source").has("name") 
                            ? articleObj.getAsJsonObject("source").get("name").getAsString() : "Unknown";
                    String title = articleObj.has("title") ? articleObj.get("title").getAsString() : "No title";
                    String description = articleObj.has("description") ? articleObj.get("description").getAsString() : "";
                    String url = articleObj.has("url") ? articleObj.get("url").getAsString() : "";
                    String publishedAt = articleObj.has("publishedAt") ? articleObj.get("publishedAt").getAsString() : "";
                    String author = articleObj.has("author") ? articleObj.get("author").getAsString() : "";
                    
                    articles.add(new NewsArticle(source, title, description, url, publishedAt, author));
                }
            }
            return articles;
        }
    }
}
