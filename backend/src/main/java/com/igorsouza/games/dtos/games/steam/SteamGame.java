package com.igorsouza.games.dtos.games.steam;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SteamGame {

    private int id;

    private String type;

    private String name;

    @JsonProperty("price")
    private SteamGamePriceOverview priceOverview;

    @JsonProperty("tiny_image")
    private String tinyImage;

    private String metascore;

    private SteamGamePlatforms platforms;

    @JsonProperty("streamingvideo")
    private boolean streamingVideo;
}
