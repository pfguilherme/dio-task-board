package io.github.pfguilherme.service;

import io.github.pfguilherme.dto.BoardColumnInfoDTO;
import io.github.pfguilherme.dto.CardDetailsDTO;
import io.github.pfguilherme.exception.CardBlockedException;
import io.github.pfguilherme.exception.CardFinishedException;
import io.github.pfguilherme.exception.EntityNotFoundException;
import io.github.pfguilherme.persistence.dao.BlockDAO;
import io.github.pfguilherme.persistence.dao.CardDAO;
import io.github.pfguilherme.persistence.entity.BoardColumnType;
import io.github.pfguilherme.persistence.entity.CardEntity;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@AllArgsConstructor
public class CardService
{
    private final Connection connection;

    public CardEntity insert(final CardEntity entity) throws SQLException
    {
        try
        {
            var dao = new CardDAO(connection);
            dao.insert(entity);
            connection.commit();

            return entity;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            connection.rollback();
            throw e;
        }
    }

    public void moveToNextColumn(final Long cardId, final List<BoardColumnInfoDTO> boardColumnsInfo) throws SQLException
    {
        try
        {
            var dao = new CardDAO(connection);
            var dtoOptional = dao.findById(cardId);
            var dto = dtoOptional.orElseThrow(
                () -> new EntityNotFoundException(("O card de id %s não foi encontrado.").formatted(cardId))
            );

            if (dto.blocked())
            {
                throw new CardBlockedException(("O card de id %s está bloqueado.").formatted(cardId));
            }

            var currentColumn = boardColumnsInfo.stream()
                .filter(columnInfo -> columnInfo.id().equals(dto.columnId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("O card informado não pertence a esse board."));

            if (currentColumn.kind().equals(BoardColumnType.FINAL))
            {
                throw new CardFinishedException("O card foi finalizado.");
            }

            var nextColumn = boardColumnsInfo.stream()
                .filter(column -> column.order() == currentColumn.order() + 1)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("O card está cancelado."));

            dao.moveToColumn(nextColumn.id(), cardId);
            connection.commit();
        }
        catch (SQLException e)
        {
            connection.rollback();
            throw e;
        }
    }

    public void cancel(final Long cardId, final Long cancelColumnId, final List<BoardColumnInfoDTO> boardColumnsInfo) throws SQLException
    {
        try
        {
            var dao = new CardDAO(connection);
            var dtoOptional = dao.findById(cardId);
            var dto = dtoOptional.orElseThrow(
                () -> new EntityNotFoundException(("O card de id %s não foi encontrado.").formatted(cardId))
            );

            if (dto.blocked())
            {
                throw new CardBlockedException(("O card de id %s está bloqueado.").formatted(cardId));
            }

            var currentColumn = boardColumnsInfo.stream()
                .filter(columnInfo -> columnInfo.id().equals(dto.columnId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("O card informado não pertence a esse board."));

            if (currentColumn.kind().equals(BoardColumnType.FINAL))
            {
                throw new CardFinishedException("O card foi finalizado.");
            }

            boardColumnsInfo.stream()
                .filter(column -> column.order() == currentColumn.order() + 1)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("O card está cancelado."));

            dao.moveToColumn(cancelColumnId, cardId);
            connection.commit();
        }
        catch (SQLException e)
        {
            connection.rollback();
            throw e;
        }
    }

    public void block(final Long id, final String reason, final List<BoardColumnInfoDTO> boardColumnsInfo) throws SQLException
    {
        try
        {
            var cardDAO = new CardDAO(connection);
            var dtoOptional = cardDAO.findById(id);
            var dto = dtoOptional.orElseThrow(
                () -> new EntityNotFoundException(("O card de id %s não foi encontrado.").formatted(id))
            );

            if (dto.blocked())
            {
                throw new CardBlockedException(("O card de id %s já está bloqueado.").formatted(id));
            }

            var currentColumn = boardColumnsInfo.stream()
                .filter(column -> column.id().equals(dto.columnId()))
                .findFirst()
                .orElseThrow();

            if (currentColumn.kind().equals(BoardColumnType.FINAL) || currentColumn.kind().equals(BoardColumnType.CANCEL))
            {
                throw new IllegalStateException(
                    ("O card não pode ser bloqueado pois está em uma coluna %s.").formatted(currentColumn.kind())
                );
            }

            var blockDAO = new BlockDAO(connection);
            blockDAO.block(id, reason);

            connection.commit();
        }
        catch (SQLException e)
        {
            connection.rollback();
            throw e;
        }
    }

    public void unblock(final Long id, final String reason) throws SQLException
    {
        try
        {
            var cardDAO = new CardDAO(connection);
            var dtoOptional = cardDAO.findById(id);
            var dto = dtoOptional.orElseThrow(
                () -> new EntityNotFoundException(("O card de id %s não foi encontrado.").formatted(id))
            );

            if ( !(dto.blocked()) )
            {
                throw new CardBlockedException(("O card de id %s já está desbloqueado.").formatted(id));
            }

            var blockDAO = new BlockDAO(connection);
            blockDAO.unblock(id, reason);

            connection.commit();
        }
        catch (SQLException e)
        {
            connection.rollback();
            throw e;
        }
    }
}
