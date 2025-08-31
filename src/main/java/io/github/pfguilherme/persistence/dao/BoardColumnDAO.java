package io.github.pfguilherme.persistence.dao;

import com.mysql.cj.jdbc.StatementImpl;
import io.github.pfguilherme.persistence.entity.BoardColumnEntity;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@RequiredArgsConstructor
public class BoardColumnDAO
{
    private final Connection connection;

    public BoardColumnEntity insert(final BoardColumnEntity entity) throws SQLException
    {
        var sql = "INSERT INTO BOARDS_COLUMNS (name, `order`, kind, board_id) VALUES (?, ?, ?, ?)";
        try (var statement = connection.prepareStatement(sql))
        {
            var index = 1;
            statement.setString(index++, entity.getName());
            statement.setInt(index++, entity.getOrder());
            statement.setString(index++, entity.getKind().name());
            statement.setLong(index++, entity.getBoard().getId());
            statement.executeUpdate();

            if (statement instanceof StatementImpl impl)
            {
                entity.setId(impl.getLastInsertID());
            }

            return entity;
        }
    }

    public List<BoardColumnEntity> findByBoardId(final Long id) throws SQLException
    {
        return null;
    }
}
