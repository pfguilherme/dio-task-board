package io.github.pfguilherme.persistence.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@Data
public class BoardEntity
{
    private Long id;
    private String name;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<BoardColumnEntity> boardColumns = new ArrayList<>();

    public BoardColumnEntity getInitialColumn()
    {
        return getFilteredColumn(column -> column.getKind().equals(BoardColumnType.INITIAL));
    }

    public BoardColumnEntity getCancelColumn()
    {
        return getFilteredColumn(column -> column.getKind().equals(BoardColumnType.CANCEL));
    }

    private BoardColumnEntity getFilteredColumn(Predicate<BoardColumnEntity> filter)
    {
        return boardColumns.stream()
            .filter(filter)
            .findFirst().orElseThrow();
    }
}
