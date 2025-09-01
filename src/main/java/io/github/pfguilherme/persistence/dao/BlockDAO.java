package io.github.pfguilherme.persistence.dao;

import io.github.pfguilherme.persistence.converter.OffsetDateTimeConverter;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.OffsetDateTime;

@AllArgsConstructor
public class BlockDAO
{
    private final Connection connection;

    public void block(final Long cardId, final String reason) throws SQLException
    {
        var sql =
            """
            INSERT INTO BLOCKS (blocked_at, block_reason, card_id) VALUES (?, ?, ?);
            """;

        try (var statement = connection.prepareStatement(sql))
        {
            var index = 1;
            statement.setTimestamp(index++, OffsetDateTimeConverter.toTimestamp(OffsetDateTime.now()));
            statement.setString(index++, reason);
            statement.setLong(index, cardId);
            statement.executeUpdate();
        }
    }

    public void unblock(final Long cardId, final String reason) throws SQLException
    {
        var sql =
            """
            UPDATE BLOCKS SET unblocked_at = ?, unblock_reason = ? WHERE card_id = ? AND unblock_reason IS NULL;
            """;

        try (var statement = connection.prepareStatement(sql))
        {
            var index = 1;
            statement.setTimestamp(index++, OffsetDateTimeConverter.toTimestamp(OffsetDateTime.now()));
            statement.setString(index++, reason);
            statement.setLong(index, cardId);
            statement.executeUpdate();
        }
    }
}
