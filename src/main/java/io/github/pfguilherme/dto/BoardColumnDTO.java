package io.github.pfguilherme.dto;

import io.github.pfguilherme.persistence.entity.BoardColumnType;

public record BoardColumnDTO(Long id, String name, BoardColumnType kind, int cardsAmount)
{
}
