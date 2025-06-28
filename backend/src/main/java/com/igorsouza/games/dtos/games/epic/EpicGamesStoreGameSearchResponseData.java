package com.igorsouza.games.dtos.games.epic;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EpicGamesStoreGameSearchResponseData {
    @JsonProperty("Catalog")
    private EpicGamesStoreSearchCatalog catalog;
}
