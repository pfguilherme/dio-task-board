package io.github.pfguilherme.service;

import io.github.pfguilherme.persistence.dao.BoardColumnDAO;
import io.github.pfguilherme.persistence.dao.BoardDAO;
import io.github.pfguilherme.persistence.entity.BoardEntity;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;

@AllArgsConstructor
public class BoardService
{
    private final Connection connection;

    public BoardEntity insert(final BoardEntity entity) throws SQLException
    {
        var boardDAO = new BoardDAO(connection);
        var boardColumnDAO = new BoardColumnDAO(connection);

        try
        {
            boardDAO.insert(entity);
            var columns = entity.getBoardColumns().stream().map(column -> {
                column.setBoard(entity);
                return column;
            }).toList();

            for (var column : columns)
            {
                boardColumnDAO.insert(column);
            }

            connection.commit();
        }
        catch (SQLException e)
        {
            connection.rollback();
            throw e;
        }

        return entity;
    }

    public boolean delete(final Long id) throws SQLException
    {
        var boardDAO = new BoardDAO(connection);

        try
        {
            if ( !(boardDAO.has(id)) )
            {
                return false;
            }

            boardDAO.delete(id);
            connection.commit();

            return true;
        }
        catch (SQLException e)
        {
            connection.rollback();
            throw e;
        }
    }
}
