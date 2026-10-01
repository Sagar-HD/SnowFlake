package com.newsaggregator.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.newsaggregator.model.NewsArticle;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.util.ArrayList;
import java.util.List;

public class NewsCatcherClient implements NewsApiClient {
    private static final String BASE_URL = "https://v3-api.newscatcherapi.com/api/latest_headlines";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private final OkHttpClient client;

    public NewsCatcherClient() {
        this.client = new OkHttpClient();
    }

    @Override
    public String getApiName() {
        return "NewsCatcher API";
    }

    @Override
    public List<NewsArticle> fetchTopNews(String apiKey) throws Exception {
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("lang", "en");
        requestBody.addProperty("page_size", 10);

        RequestBody body = RequestBody.create(requestBody.toString(), JSON);
        Request request = new Request.Builder()
                .url(BASE_URL)
                .addHeader("x-api-token", apiKey)
                .post(body)
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
                    
                    String source = articleObj.has("media") ? articleObj.get("media").getAsString() : "Unknown";
                    String title = articleObj.has("title") ? articleObj.get("title").getAsString() : "No title";
                    String description = articleObj.has("excerpt") ? articleObj.get("excerpt").getAsString() : "";
                    String url = articleObj.has("link") ? articleObj.get("link").getAsString() : "";
                    String publishedAt = articleObj.has("published_date") ? articleObj.get("published_date").getAsString() : "";
                    String author = articleObj.has("author") ? articleObj.get("author").getAsString() : "";
                    
                    articles.add(new NewsArticle(source, title, description, url, publishedAt, author));
                }
            }
            return articles;
        }
    }
}
