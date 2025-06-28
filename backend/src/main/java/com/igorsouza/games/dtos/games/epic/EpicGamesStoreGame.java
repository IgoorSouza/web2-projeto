package com.igorsouza.games.dtos.games.epic;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class EpicGamesStoreGame {
    private String title;
    private String productSlug;
    private String urlSlug;
    private EpicGamesStoreGameCatalogNs catalogNs;
    private EpicGamesStoreGamePrice price;
    private List<EpicGamesStoreGameTag> tags;
    private List<EpicGamesStoreGameCategory> categories;
    private List<EpicGamesStoreGameImage> keyImages;
}
