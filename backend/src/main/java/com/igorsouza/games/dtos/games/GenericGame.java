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
public class GenericGame {
    private String identifier;
    private String title;
    private String url;
    private String image;
    private GamePlatform platform;
    private double initialPrice;
    private double discountPrice;
    private int discountPercent;
}
