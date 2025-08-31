package io.github.pfguilherme.service;

import io.github.pfguilherme.persistence.dao.BoardColumnDAO;
import io.github.pfguilherme.persistence.dao.BoardDAO;
import io.github.pfguilherme.persistence.entity.BoardEntity;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

@AllArgsConstructor
public class BoardQueryService
{
    private final Connection connection;

    public Optional<BoardEntity> findById(final Long id) throws SQLException
    {
        var boardDAO = new BoardDAO(connection);
        var boardColumnDAO = new BoardColumnDAO(connection);

        var optional = boardDAO.findById(id);
        if (optional.isPresent())
        {
            var entity = optional.get();
            entity.setBoardColumns(boardColumnDAO.findByBoardId(entity.getId()));

            return Optional.of(entity);
        }

        return Optional.empty();
    }
}
