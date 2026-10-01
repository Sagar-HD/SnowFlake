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

public class GuardianClient implements NewsApiClient {
    private static final String BASE_URL = "https://content.guardianapis.com/search";
    private final OkHttpClient client;

    public GuardianClient() {
        this.client = new OkHttpClient();
    }

    @Override
    public String getApiName() {
        return "The Guardian Open Platform";
    }

    @Override
    public List<NewsArticle> fetchTopNews(String apiKey) throws Exception {
        String apiUrl = BASE_URL + "?api-key=" + apiKey + "&page-size=10&show-fields=headline,byline,shortUrl";
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
            if (jsonResponse.has("response") && jsonResponse.getAsJsonObject("response").has("results")) {
                JsonArray resultsArray = jsonResponse.getAsJsonObject("response").getAsJsonArray("results");
                for (int i = 0; i < Math.min(10, resultsArray.size()); i++) {
                    JsonObject articleObj = resultsArray.get(i).getAsJsonObject();
                    
                    String source = "The Guardian";
                    String title = articleObj.has("webTitle") ? articleObj.get("webTitle").getAsString() : "No title";
                    String description = "";
                    String url = articleObj.has("webUrl") ? articleObj.get("webUrl").getAsString() : "";
                    String publishedAt = articleObj.has("webPublicationDate") ? articleObj.get("webPublicationDate").getAsString() : "";
                    String author = "";
                    
                    if (articleObj.has("fields")) {
                        JsonObject fields = articleObj.getAsJsonObject("fields");
                        if (fields.has("byline")) {
                            author = fields.get("byline").getAsString();
                        }
                        if (fields.has("headline")) {
                            title = fields.get("headline").getAsString();
                        }
                    }
                    
                    articles.add(new NewsArticle(source, title, description, url, publishedAt, author));
                }
            }
            return articles;
        }
    }
}
