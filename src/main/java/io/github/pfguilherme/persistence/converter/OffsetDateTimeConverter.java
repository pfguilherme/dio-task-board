package io.github.pfguilherme.persistence.converter;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class OffsetDateTimeConverter
{
    public static OffsetDateTime toOffsetDateTime(final Timestamp timestamp)
    {
        return Objects.nonNull(timestamp) ? OffsetDateTime.ofInstant(timestamp.toInstant(), ZoneOffset.UTC) : null;
    }

    public static Timestamp toTimestamp(final OffsetDateTime offsetDateTime)
    {
        return Objects.nonNull(offsetDateTime) ?
            Timestamp.valueOf(
                offsetDateTime.atZoneSameInstant(ZoneOffset.UTC).toLocalDateTime()
            ) : null;
    }
}
