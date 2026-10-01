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

public class NewsDataClient implements NewsApiClient {
    private static final String BASE_URL = "https://newsdata.io/api/1/latest";
    private final OkHttpClient client;

    public NewsDataClient() {
        this.client = new OkHttpClient();
    }

    @Override
    public String getApiName() {
        return "NewsData.io";
    }

    @Override
    public List<NewsArticle> fetchTopNews(String apiKey) throws Exception {
        String apiUrl = BASE_URL + "?apikey=" + apiKey + "&language=en";
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
            if (jsonResponse.has("results")) {
                JsonArray resultsArray = jsonResponse.getAsJsonArray("results");
                for (int i = 0; i < Math.min(10, resultsArray.size()); i++) {
                    JsonObject articleObj = resultsArray.get(i).getAsJsonObject();
                    
                    String source = articleObj.has("source_id") ? articleObj.get("source_id").getAsString() : "Unknown";
                    String title = articleObj.has("title") ? articleObj.get("title").getAsString() : "No title";
                    String description = articleObj.has("description") ? articleObj.get("description").getAsString() : "";
                    String url = articleObj.has("link") ? articleObj.get("link").getAsString() : "";
                    String publishedAt = articleObj.has("pubDate") ? articleObj.get("pubDate").getAsString() : "";
                    String author = "";
                    
                    articles.add(new NewsArticle(source, title, description, url, publishedAt, author));
                }
            }
            return articles;
        }
    }
}
