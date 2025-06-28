package com.igorsouza.games.dtos.games.steam;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SteamGameDetails {

    private String identifier;

    private String name;

    @JsonProperty("is_free")
    private boolean isFree;

    @JsonProperty("header_image")
    private String headerImage;

    @JsonProperty("price_overview")
    private SteamGamePriceOverview priceOverview;

    private String url;
}
