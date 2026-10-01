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

public class CurrentsClient implements NewsApiClient {
    private static final String BASE_URL = "https://api.currentsapi.services/v1/latest-news";
    private final OkHttpClient client;

    public CurrentsClient() {
        this.client = new OkHttpClient();
    }

    @Override
    public String getApiName() {
        return "Currents API";
    }

    @Override
    public List<NewsArticle> fetchTopNews(String apiKey) throws Exception {
        String apiUrl = BASE_URL + "?language=en&page_size=10";
        Request request = new Request.Builder()
                .url(apiUrl)
                .addHeader("Authorization", "Bearer " + apiKey)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new Exception("Failed to fetch news: " + response.code());
            }

            String responseBody = response.body().string();
            JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
            
            List<NewsArticle> articles = new ArrayList<>();
            if (jsonResponse.has("news")) {
                JsonArray newsArray = jsonResponse.getAsJsonArray("news");
                for (int i = 0; i < Math.min(10, newsArray.size()); i++) {
                    JsonObject articleObj = newsArray.get(i).getAsJsonObject();
                    
                    String source = articleObj.has("author") ? articleObj.get("author").getAsString() : "Unknown";
                    String title = articleObj.has("title") ? articleObj.get("title").getAsString() : "No title";
                    String description = articleObj.has("description") ? articleObj.get("description").getAsString() : "";
                    String url = articleObj.has("url") ? articleObj.get("url").getAsString() : "";
                    String publishedAt = articleObj.has("published") ? articleObj.get("published").getAsString() : "";
                    String author = articleObj.has("author") ? articleObj.get("author").getAsString() : "";
                    
                    articles.add(new NewsArticle(source, title, description, url, publishedAt, author));
                }
            }
            return articles;
        }
    }
}
