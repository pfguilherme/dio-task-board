package io.github.pfguilherme.service;

import io.github.pfguilherme.dto.CardDetailsDTO;
import io.github.pfguilherme.persistence.dao.CardDAO;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

@AllArgsConstructor
public class CardQueryService
{
    private Connection connection;

    public Optional<CardDetailsDTO> findById(final Long id) throws SQLException
    {
        var dao = new CardDAO(connection);
        return dao.findById(id);
    }
}
