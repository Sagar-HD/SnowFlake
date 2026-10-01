package com.newsaggregator.api;

import com.newsaggregator.model.NewsArticle;
import java.util.List;

public interface NewsApiClient {
    String getApiName();
    List<NewsArticle> fetchTopNews(String apiKey) throws Exception;
}
