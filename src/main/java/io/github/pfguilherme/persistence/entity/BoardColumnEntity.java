package io.github.pfguilherme.persistence.entity;

import lombok.Data;

@Data
public class BoardColumnEntity
{
    private Long id;
    private String name;
    private int order;
    private BoardColumnKind kind;
    private BoardEntity board = new BoardEntity();
}
