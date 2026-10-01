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

public class WorldNewsApiClient implements NewsApiClient {
    private static final String BASE_URL = "https://api.worldnewsapi.com/top-news";
    private final OkHttpClient client;

    public WorldNewsApiClient() {
        this.client = new OkHttpClient();
    }

    @Override
    public String getApiName() {
        return "World News API";
    }

    @Override
    public List<NewsArticle> fetchTopNews(String apiKey) throws Exception {
        String apiUrl = BASE_URL + "?source-country=us&language=en&api-key=" + apiKey;
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
            if (jsonResponse.has("top_news")) {
                JsonArray topNewsArray = jsonResponse.getAsJsonArray("top_news");
                for (int i = 0; i < Math.min(10, topNewsArray.size()); i++) {
                    JsonObject clusterObj = topNewsArray.get(i).getAsJsonObject();
                    if (clusterObj.has("news")) {
                        JsonArray newsArray = clusterObj.getAsJsonArray("news");
                        if (newsArray.size() > 0) {
                            JsonObject articleObj = newsArray.get(0).getAsJsonObject();
                            
                            String source = articleObj.has("authors") && articleObj.getAsJsonArray("authors").size() > 0
                                    ? articleObj.getAsJsonArray("authors").get(0).getAsString() : "Unknown";
                            String title = articleObj.has("title") ? articleObj.get("title").getAsString() : "No title";
                            String description = articleObj.has("summary") ? articleObj.get("summary").getAsString() : "";
                            String url = articleObj.has("url") ? articleObj.get("url").getAsString() : "";
                            String publishedAt = articleObj.has("publish_date") ? articleObj.get("publish_date").getAsString() : "";
                            String author = source;
                            
                            articles.add(new NewsArticle(source, title, description, url, publishedAt, author));
                        }
                    }
                }
            }
            return articles;
        }
    }
}
