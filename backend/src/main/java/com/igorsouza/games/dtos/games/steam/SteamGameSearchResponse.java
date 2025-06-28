package com.igorsouza.games.dtos.games.steam;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SteamGameSearchResponse {
    private int total;
    private List<SteamGame> items;
}
