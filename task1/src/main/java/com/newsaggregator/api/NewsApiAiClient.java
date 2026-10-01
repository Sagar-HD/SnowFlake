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

public class NewsApiAiClient implements NewsApiClient {
    private static final String BASE_URL = "https://eventregistry.org/api/v1/article/getArticles";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private final OkHttpClient client;

    public NewsApiAiClient() {
        this.client = new OkHttpClient();
    }

    @Override
    public String getApiName() {
        return "NewsAPI.ai";
    }

    @Override
    public List<NewsArticle> fetchTopNews(String apiKey) throws Exception {
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("apiKey", apiKey);
        requestBody.addProperty("resultType", "articles");
        requestBody.addProperty("articlesCount", 10);
        requestBody.addProperty("articlesSortBy", "date");
        JsonArray dataTypeArray = new JsonArray();
        dataTypeArray.add("news");
        dataTypeArray.add("pr");
        requestBody.add("dataType", dataTypeArray);

        RequestBody body = RequestBody.create(requestBody.toString(), JSON);
        Request request = new Request.Builder()
                .url(BASE_URL)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new Exception("Failed to fetch news: " + response.code());
            }

            String responseBody = response.body().string();
            JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
            
            List<NewsArticle> articles = new ArrayList<>();
            if (jsonResponse.has("articles") && jsonResponse.getAsJsonObject("articles").has("results")) {
                JsonArray resultsArray = jsonResponse.getAsJsonObject("articles").getAsJsonArray("results");
                for (int i = 0; i < Math.min(10, resultsArray.size()); i++) {
                    JsonObject articleObj = resultsArray.get(i).getAsJsonObject();
                    
                    String source = articleObj.has("source") && articleObj.getAsJsonObject("source").has("title")
                            ? articleObj.getAsJsonObject("source").get("title").getAsString() : "Unknown";
                    String title = articleObj.has("title") ? articleObj.get("title").getAsString() : "No title";
                    String description = articleObj.has("body") ? articleObj.get("body").getAsString() : "";
                    String url = articleObj.has("url") ? articleObj.get("url").getAsString() : "";
                    String publishedAt = articleObj.has("dateTime") ? articleObj.get("dateTime").getAsString() : "";
                    String author = articleObj.has("author") ? articleObj.get("author").getAsString() : "";
                    
                    articles.add(new NewsArticle(source, title, description, url, publishedAt, author));
                }
            }
            return articles;
        }
    }
}
