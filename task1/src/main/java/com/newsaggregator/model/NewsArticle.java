package com.newsaggregator.model;

public class NewsArticle {
    private String source;
    private String title;
    private String description;
    private String url;
    private String publishedAt;
    private String author;

    public NewsArticle(String source, String title, String description, String url, String publishedAt, String author) {
        this.source = source;
        this.title = title;
        this.description = description;
        this.url = url;
        this.publishedAt = publishedAt;
        this.author = author;
    }

    public String getSource() {
        return source;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public String getAuthor() {
        return author;
    }

    @Override
    public String toString() {
        return String.format(
            "Source: %s\nTitle: %s\nDescription: %s\nURL: %s\nPublished: %s\nAuthor: %s\n---",
            source, title, description != null ? description.substring(0, Math.min(100, description.length())) : "N/A",
            url, publishedAt, author != null ? author : "N/A"
        );
    }
}
