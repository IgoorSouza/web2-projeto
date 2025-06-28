package com.igorsouza.games.dtos.games;

import com.igorsouza.games.enums.GamePlatform;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WishlistGame {
    private String platformIdentifier;
    private GamePlatform platform;
}
