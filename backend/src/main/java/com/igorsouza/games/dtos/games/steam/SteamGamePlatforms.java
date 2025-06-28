package com.igorsouza.games.dtos.games.steam;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SteamGamePlatforms {
    private boolean windows;
    private boolean mac;
    private boolean linux;
}
