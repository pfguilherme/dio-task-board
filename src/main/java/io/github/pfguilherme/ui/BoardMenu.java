package io.github.pfguilherme.ui;

import io.github.pfguilherme.dto.BoardColumnInfoDTO;
import io.github.pfguilherme.persistence.config.ConnectionConfig;
import io.github.pfguilherme.persistence.entity.BoardColumnEntity;
import io.github.pfguilherme.persistence.entity.BoardEntity;
import io.github.pfguilherme.persistence.entity.CardEntity;
import io.github.pfguilherme.service.BoardColumnQueryService;
import io.github.pfguilherme.service.BoardQueryService;
import io.github.pfguilherme.service.CardQueryService;
import io.github.pfguilherme.service.CardService;
import lombok.AllArgsConstructor;

import java.sql.SQLException;
import java.util.Scanner;

@AllArgsConstructor
public class BoardMenu
{
    private final Scanner scanner = new Scanner(System.in).useDelimiter("\n");
    private final BoardEntity entity;

    public void execute()
    {
        try {
            System.out.printf("Selecione uma operação para realizar no board de id %s.\n", entity.getId());

            var option = -1;
            while (option != 9) {
                System.out.println("1 - Criar um card");
                System.out.println("2 - Mover um card");
                System.out.println("3 - Bloquear um card");
                System.out.println("4 - Desbloquear um card");
                System.out.println("5 - Cancelar um card");
                System.out.println("6 - Ver board");
                System.out.println("7 - Ver coluna com cards");
                System.out.println("8 - Ver card");
                System.out.println("9 - Voltar para o menu anterior");
                System.out.println("10 - Sair");

                option = scanner.nextInt();
                switch (option) {
                    case 1 -> createCard();
                    case 2 -> moveCardToNextColumn();
                    case 3 -> blockCard();
                    case 4 -> unblockCard();
                    case 5 -> cancelCard();
                    case 6 -> showBoard();
                    case 7 -> showColumn();
                    case 8 -> showCard();
                    case 9 -> System.out.println("Saindo do menu...");
                    case 10 -> System.exit(0);
                    default -> System.out.println("Opção inválida");
                }
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            System.exit(0);
        }
    }

    private void createCard() throws SQLException
    {
        var card = new CardEntity();
        System.out.println("Informe o título do card:");
        card.setTitle(scanner.next());
        System.out.println("Informe a descrição do card:");
        card.setDescription(scanner.next());

        var initialColumn = entity.getInitialColumn();
        card.setBoardColumn(initialColumn);

        try (var connection = ConnectionConfig.getConnection())
        {
            var cardService = new CardService(connection);
            cardService.insert(card);
        }
    }

    private void moveCardToNextColumn() throws SQLException
    {
        System.out.println("Digite o id do card que será movido:");
        var cardId = scanner.nextLong();
        var boardColumnsInfo = entity.getBoardColumns().stream()
            .map(column -> new BoardColumnInfoDTO(column.getId(), column.getOrder(), column.getKind()))
            .toList();

        try (var connection = ConnectionConfig.getConnection())
        {
            var cardService = new CardService(connection);
            cardService.moveToNextColumn(cardId, boardColumnsInfo);
        }
        catch (RuntimeException e)
        {
            System.out.println(e.getMessage());
        }
    }

    private void blockCard() throws SQLException
    {
        System.out.println("Digite o id do card:");
        var cardId = scanner.nextLong();
        System.out.println("Digite o motivo do bloqueio:");
        var reason = scanner.next();

        var boardColumnsInfo = entity.getBoardColumns().stream()
            .map(column -> new BoardColumnInfoDTO(column.getId(), column.getOrder(), column.getKind()))
            .toList();

        try (var connection = ConnectionConfig.getConnection())
        {
            var cardService = new CardService(connection);
            cardService.block(cardId, reason, boardColumnsInfo);
        }
        catch (RuntimeException e)
        {
            System.out.println(e.getMessage());
        }
    }

    private void unblockCard() throws SQLException
    {
        System.out.println("Digite o id do card:");
        var cardId = scanner.nextLong();
        System.out.println("Digite o motivo do desbloqueio:");
        var reason = scanner.next();

        try (var connection = ConnectionConfig.getConnection())
        {
            var cardService = new CardService(connection);
            cardService.unblock(cardId, reason);
        }
        catch (RuntimeException e)
        {
            System.out.println(e.getMessage());
        }
    }

    private void cancelCard() throws SQLException
    {
        System.out.println("Digite o id do card que será cancelado:");
        var cardId = scanner.nextLong();
        var cancelColumn = entity.getCancelColumn();

        var boardColumnsInfo = entity.getBoardColumns().stream()
            .map(column -> new BoardColumnInfoDTO(column.getId(), column.getOrder(), column.getKind()))
            .toList();

        try (var connection = ConnectionConfig.getConnection())
        {
            var cardService = new CardService(connection);
            cardService.cancel(cardId, cancelColumn.getId(), boardColumnsInfo);
        }
        catch (RuntimeException e)
        {
            System.out.println(e.getMessage());
        }
    }

    private void showBoard() throws SQLException
    {
        try (var connection = ConnectionConfig.getConnection())
        {
            var optional = new BoardQueryService(connection).showBoardDetails(entity.getId());
            optional.ifPresent(dto -> {
                System.out.printf("Board [%s, %s]\n", dto.id(), dto.name());
                dto.columns().forEach(column -> {
                    System.out.printf(
                        "Column [%s]\n -> Tipo: [%s];\n -> Tem %s cards\n;",
                        column.name(),
                        column.kind(),
                        column.cardsAmount()
                    );
                });
            });
        }
    }

    private void showColumn() throws SQLException
    {
        var columnIds = entity.getBoardColumns().stream().map(BoardColumnEntity::getId).toList();
        var selectedColumnId = -1L;
        while ( !(columnIds.contains(selectedColumnId)) )
        {
            System.out.printf("Escolha uma coluna do board %s:\n", entity.getName());
            entity.getBoardColumns().forEach(column ->
                System.out.printf("%s - %s [%s]\n", column.getId(), column.getName(), column.getKind())
            );
            selectedColumnId = scanner.nextLong();
        }

        try (var connection = ConnectionConfig.getConnection())
        {
            var queryService = new BoardColumnQueryService(connection) ;
            var columnOptional = queryService.findById(selectedColumnId);
            columnOptional.ifPresent(column -> {
                System.out.printf("Coluna %s de tipo %s:\n", column.getName(), column.getKind());
                for (var card : column.getCards()) {
                    System.out.printf(
                        "\t-> Card %s:\n\t\t- Title: %s\n\t\t- Descrição: %s\n",
                        card.getId(),
                        card.getTitle(),
                        card.getDescription()
                    );
                }
            });
        }
    }

    private void showCard() throws SQLException
    {
        System.out.println("Informe o id do card que desejar visualizar:");
        var selectedCardId = scanner.nextLong();
        try (var connection = ConnectionConfig.getConnection())
        {
            var cardQueryService = new CardQueryService(connection);
            var optional = cardQueryService.findById(selectedCardId);
            optional.ifPresentOrElse(
                dto -> {
                    System.out.printf("Card de id %s:\n", dto.id());
                    System.out.printf("\t-> Title: %s\n", dto.title());
                    System.out.printf("\t-> Descrição: %s\n", dto.description());
                    System.out.printf(dto.blocked() ?
                        "\t-> O cartão está bloquado devido a " + dto.blockReason() + "\n" :
                        "O cartão não está bloquado.\n"
                    );
                    System.out.printf("\t-> Número de bloqueio(s): %s\n", dto.blocksAmount());
                    System.out.printf("\t-> Coluna de id %s:\n", dto.columnId());
                    System.out.printf("\t\t-> Nome: %s", dto.columnName());
                },
                () -> System.out.printf("Não foi encontrado um card de id %s.\n", selectedCardId)
            );
        }
    }
}
