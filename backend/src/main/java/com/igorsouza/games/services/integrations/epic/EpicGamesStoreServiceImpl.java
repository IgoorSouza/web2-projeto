package com.igorsouza.games.services.integrations.epic;

import com.igorsouza.games.dtos.games.epic.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class EpicGamesStoreServiceImpl implements EpicGamesStoreService {

    @Override
    public List<EpicGamesStoreGame> getGames(String gameName) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<EpicGamesStoreGameSearchResponse> response = restTemplate.postForEntity(
                "https://graphql.epicgames.com/graphql",
                getGraphQLRequest(gameName),
                EpicGamesStoreGameSearchResponse.class);

        return response.getBody().getData().getCatalog().getSearchStore().getElements();
    }

    @Override
    public EpicGamesStoreGame getGameDetails(String identifier) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<EpicGamesStoreGameSearchResponse> response = restTemplate.postForEntity(
                "https://graphql.epicgames.com/graphql",
                getGraphQLRequest(identifier),
                EpicGamesStoreGameSearchResponse.class);

        return response.getBody().getData().getCatalog().getSearchStore().getElements().getFirst();
    }

    private Map<String, Object> getGraphQLRequest(String gameName) {
        String query = """
            query searchStoreQuery($keyword: String, $locale: String!, $country: String!) {
              Catalog {
                searchStore(
                  keywords: $keyword,
                  locale: $locale,
                  country: $country,
                  sortBy: "relevancy"
                  sortDir: "DESC"
                  start: 0,
                  count: 10
                ) {
                  elements {
                    title
                    productSlug
                    urlSlug
                    keyImages {
                      url
                    }
                    categories {
                      path
                    }
                    tags {
                      name
                    }
                    price(country: $country) {
                      totalPrice {
                        discountPrice
                        originalPrice
                      }
                    }
                    catalogNs {
                      mappings {
                        pageSlug
                        pageType
                      }
                    }
                  }
                }
              }
            }
        """;

        Map<String, Object> variables = Map.of("keyword", gameName, "locale", "pt-BR", "country", "BR");
        return Map.of("query", query, "variables", variables, "operationName", "searchStoreQuery");
    }
}
