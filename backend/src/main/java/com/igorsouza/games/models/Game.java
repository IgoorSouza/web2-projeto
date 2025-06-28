package com.igorsouza.games.models;

import com.igorsouza.games.enums.GamePlatform;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "games")
public class Game {

    @EmbeddedId
    private GameId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public String getPlatformIdentifier() {
        return id.getPlatformIdentifier();
    }

    public GamePlatform getPlatform() {
        return id.getPlatform();
    }

    public UUID getUserId() {
        return user.getId();
    }
}
