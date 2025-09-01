package io.github.pfguilherme.dto;

import io.github.pfguilherme.persistence.entity.BoardColumnType;

public record BoardColumnInfoDTO(Long id, int order, BoardColumnType kind)
{
}
