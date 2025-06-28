package com.igorsouza.games.models;

import com.igorsouza.games.enums.GamePlatform;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class GameId implements Serializable {

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "platform_identifier", nullable = false)
    private String platformIdentifier;

    @Enumerated(EnumType.STRING)
    @Column(name = "platform", nullable = false)
    private GamePlatform platform;
}
