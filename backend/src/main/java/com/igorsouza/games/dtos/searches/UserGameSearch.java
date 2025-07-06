package com.igorsouza.games.dtos.searches;

import com.igorsouza.games.enums.GamePlatform;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserGameSearch {
    private String gameName;
    private GamePlatform platform;
    private Date date;
}
