package com.igorsouza.games.dtos.games;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    private UUID id;
    private String content;
    private boolean aiGenerated;
    private Date createdAt;
    private Date updatedAt;
}
