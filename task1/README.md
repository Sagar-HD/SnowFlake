# News Aggregator - Java Application

A Java application that fetches top 10 news articles from 10 different news APIs and displays them in the console.

## Supported APIs

1. **NewsAPI.org** - https://newsapi.org/
2. **GNews** - https://gnews.io/
3. **NewsData.io** - https://newsdata.io/
4. **Currents API** - https://currentsapi.services/
5. **The News API** - https://www.thenewsapi.com/
6. **Mediastack** - https://mediastack.com/
7. **The Guardian Open Platform** - https://open-platform.theguardian.com/
8. **World News API** - https://worldnewsapi.com/
9. **NewsAPI.ai** - https://newsapi.ai/
10. **NewsCatcher API** - https://www.newscatcherapi.com/

## Prerequisites

- Java 11 or higher
- Maven 3.6 or higher

## Setup

1. Clone or download this project

2. Install dependencies:
   ```bash
   mvn clean install
   ```

3. Configure API keys:
   - Open `config.properties` in the project root
   - Replace `YOUR_API_KEY` placeholders with your actual API keys from each service
   - You need to sign up for each API service to get your API keys

## Running the Application

```bash
mvn exec:java -Dexec.mainClass="com.newsaggregator.NewsAggregatorApp"
```

Or compile and run:

```bash
mvn clean package
java -jar target/news-aggregator-1.0-SNAPSHOT.jar
```

## Project Structure

```
task1/
├── pom.xml                                    # Maven configuration
├── config.properties                          # API keys configuration
├── README.md                                  # This file
└── src/main/java/com/newsaggregator/
    ├── NewsAggregatorApp.java                 # Main application
    ├── model/
    │   └── NewsArticle.java                   # News article model
    └── api/
        ├── NewsApiClient.java                 # Interface for API clients
        ├── NewsApiOrgClient.java              # NewsAPI.org client
        ├── GNewsClient.java                   # GNews client
        ├── NewsDataClient.java                # NewsData.io client
        ├── CurrentsClient.java                # Currents API client
        ├── TheNewsApiClient.java              # The News API client
        ├── MediastackClient.java              # Mediastack client
        ├── GuardianClient.java                # Guardian Open Platform client
        ├── WorldNewsApiClient.java            # World News API client
        ├── NewsApiAiClient.java               # NewsAPI.ai client
        └── NewsCatcherClient.java             # NewsCatcher API client
```

## Getting API Keys

You need to sign up for each API service to get your API keys:

1. **NewsAPI.org**: https://newsapi.org/register
2. **GNews**: https://gnews.io/
3. **NewsData.io**: https://newsdata.io/register
4. **Currents API**: https://currentsapi.services/
5. **The News API**: https://www.thenewsapi.com/
6. **Mediastack**: https://mediastack.com/
7. **The Guardian**: https://open-platform.theguardian.com/access
8. **World News API**: https://worldnewsapi.com/
9. **NewsAPI.ai**: https://newsapi.ai/
10. **NewsCatcher**: https://www.newscatcherapi.com/

Most services offer free tiers with limited requests per day.

## Dependencies

- **OkHttp 4.12.0** - HTTP client for making API requests
- **Gson 2.10.1** - JSON parsing library

## Output

The application will display the top 10 news articles from each API in the console, including:
- Article title
- Source name
- Author (if available)
- Description
- URL
- Publication date

## Error Handling

If an API key is not configured or is invalid, the application will display an error message and continue to the next API.

## Notes

- Some APIs have rate limits on their free tiers
- The application makes sequential requests to each API
- Each API may return different numbers of articles based on their free tier limits
