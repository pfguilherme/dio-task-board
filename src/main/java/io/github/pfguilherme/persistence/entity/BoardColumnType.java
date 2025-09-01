package io.github.pfguilherme.persistence.entity;

import java.util.stream.Stream;

public enum BoardColumnType
{
    INITIAL,
    FINAL,
    CANCEL,
    PENDING;

    public static BoardColumnType findByName(final String name)
    {
        return Stream.of(BoardColumnType.values())
            .filter(kind -> kind.name().equals(name))
            .findFirst().orElseThrow();
    }
}
