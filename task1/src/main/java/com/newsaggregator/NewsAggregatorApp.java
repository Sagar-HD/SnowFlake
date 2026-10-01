package com.newsaggregator;

import com.newsaggregator.api.*;
import com.newsaggregator.model.NewsArticle;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class ApiResult {
    String apiName;
    NewsArticle article;
    String error;
    boolean success;

    ApiResult(String apiName, NewsArticle article, String error, boolean success) {
        this.apiName = apiName;
        this.article = article;
        this.error = error;
        this.success = success;
    }
}

public class NewsAggregatorApp {


    public static void main(String[] args) {
        Properties props = loadConfig();
        if (props == null) {
            System.err.println("Failed to load configuration. Exiting.");
            return;
        }

        NewsApiClient[] clients = {
            new NewsApiOrgClient(),
            new GNewsClient(),
            new NewsDataClient(),
            new CurrentsClient(),
            new TheNewsApiClient(),
            new MediastackClient(),
            new GuardianClient(),
            new WorldNewsApiClient(),
            new NewsApiAiClient(),
            new NewsCatcherClient()
        };

        String[] apiKeys = {
            props.getProperty("newsapi.key"),
            props.getProperty("gnews.key"),
            props.getProperty("newsdata.key"),
            props.getProperty("currents.key"),
            props.getProperty("thenewsapi.key"),
            props.getProperty("mediastack.key"),
            props.getProperty("guardian.key"),
            props.getProperty("worldnewsapi.key"),
            props.getProperty("newsapi.ai.key"),
            props.getProperty("newscatcher.key")
        };

        System.out.println("=".repeat(80));
        System.out.println("NEWS AGGREGATOR - Fetching 1 News from each of 10 APIs (PARALLEL)");
        System.out.println("=".repeat(80));
        System.out.println();

        List<ApiResult> results = Collections.synchronizedList(new ArrayList<>());
        ExecutorService executor = Executors.newFixedThreadPool(clients.length);

        List<CompletableFuture<Void>> futures = IntStream.range(0, clients.length)
            .mapToObj(i -> CompletableFuture.runAsync(() -> {
                NewsApiClient client = clients[i];
                String apiKey = apiKeys[i];

                if (apiKey == null || apiKey.trim().isEmpty() ||
                    apiKey.equals("YOUR_" + client.getApiName().toUpperCase().replace(" ", "_").replace(".", "_") + "_KEY") ||
                    apiKey.startsWith("YOUR_")) {
                    results.add(new ApiResult(client.getApiName(), null, "API key not configured", false));
                    return;
                }

                try {
                    List<NewsArticle> articles = client.fetchTopNews(apiKey);

                    if (articles != null && !articles.isEmpty()) {
                        results.add(new ApiResult(client.getApiName(), articles.get(0), null, true));
                    } else {
                        results.add(new ApiResult(client.getApiName(), null, "No articles found", false));
                    }
                } catch (java.net.SocketTimeoutException e) {
                    results.add(new ApiResult(client.getApiName(), null, "Request timed out", false));
                } catch (java.net.UnknownHostException e) {
                    results.add(new ApiResult(client.getApiName(), null, "Could not resolve host", false));
                } catch (java.io.IOException e) {
                    results.add(new ApiResult(client.getApiName(), null, "Network error: " + e.getMessage(), false));
                } catch (Exception e) {
                    results.add(new ApiResult(client.getApiName(), null, e.getMessage(), false));
                }
            }, executor))
            .collect(Collectors.toList());

        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .get(30, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            System.err.println("Error: Overall operation timed out after 30 seconds.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Error: Operation was interrupted.");
        } catch (Exception e) {
            System.err.println("Error: Unexpected error occurred - " + e.getMessage());
        } finally {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                executor.shutdownNow();
            }
        }

        System.out.println();
        System.out.println("=".repeat(80));
        System.out.println("RESULTS FROM 10 APIs");
        System.out.println("=".repeat(80));
        System.out.println();

        for (ApiResult result : results) {
            System.out.println("─".repeat(80));
            System.out.println("API: " + result.apiName);
            System.out.println("─".repeat(80));

            if (result.success && result.article != null) {
                System.out.println("\n✓ SUCCESS");
                System.out.println("\nTitle: " + result.article.getTitle());
                System.out.println("Source: " + result.article.getSource());
                if (result.article.getAuthor() != null && !result.article.getAuthor().isEmpty()) {
                    System.out.println("Author: " + result.article.getAuthor());
                }
                if (result.article.getDescription() != null && !result.article.getDescription().isEmpty()) {
                    System.out.println("Description: " + result.article.getDescription());
                }
                System.out.println("URL: " + result.article.getUrl());
                System.out.println("Published: " + result.article.getPublishedAt());
            } else {
                System.out.println("\n✗ FAILED");
                System.out.println("Error: " + result.error);
            }
            System.out.println();
        }

        System.out.println("=".repeat(80));
        long successCount = results.stream().filter(r -> r.success).count();
        System.out.println("Successful: " + successCount + "/10");
        System.out.println("Failed: " + (results.size() - successCount) + "/10");
        System.out.println("News aggregation complete!");
        System.out.println("=".repeat(80));
    }

    private static Properties loadConfig() {
        Properties props = new Properties();
        try (InputStream input = new FileInputStream("config.properties")) {
            props.load(input);
            return props;
        } catch (java.io.FileNotFoundException e) {
            System.err.println("Error: config.properties file not found in project root.");
        } catch (java.io.IOException e) {
            System.err.println("Error: Failed to read config.properties - " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error: Unexpected error loading configuration - " + e.getMessage());
        }
        return null;
    }
}
