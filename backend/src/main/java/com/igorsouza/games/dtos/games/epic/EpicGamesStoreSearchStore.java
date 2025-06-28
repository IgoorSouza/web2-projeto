package com.igorsouza.games.dtos.games.epic;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class EpicGamesStoreSearchStore {
    List<EpicGamesStoreGame> elements;
}
