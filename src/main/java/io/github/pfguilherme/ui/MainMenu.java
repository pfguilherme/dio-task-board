package io.github.pfguilherme.ui;

import io.github.pfguilherme.persistence.config.ConnectionConfig;
import io.github.pfguilherme.persistence.entity.BoardColumnEntity;
import io.github.pfguilherme.persistence.entity.BoardColumnType;
import io.github.pfguilherme.persistence.entity.BoardEntity;
import io.github.pfguilherme.service.BoardQueryService;
import io.github.pfguilherme.service.BoardService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MainMenu
{
    private final Scanner scanner = new Scanner(System.in);

    public void execute() throws SQLException
    {
        System.out.println("Escolha uma opção");

        var option = -1;
        while (true)
        {
            System.out.println("1 - Criar novo board");
            System.out.println("2 - Selecionar um board existente");
            System.out.println("3 - Excluir um board");
            System.out.println("4 - Sair");

            option = scanner.nextInt();
            switch (option)
            {
                case 1 -> createBoard();
                case 2 -> selectBoard();
                case 3 -> deleteBoard();
                case 4 -> System.exit(0);
                default -> System.out.println("Opção inválida");
            }
        }
    }

    private void createBoard() throws SQLException
    {
        System.out.println("Informe o nome do seu board.");
        BoardEntity entity = new BoardEntity();
        entity.setName(scanner.next());

        System.out.println("Se seu board terá mais do que as 3 colunas padrões, digite quantas, senão digite 0.");
        var extraColumnCount = scanner.nextInt();

        List<BoardColumnEntity> columns = new ArrayList<>();

        // Coluna inicial
        System.out.println("Informe o nome da coluna inicial do board.");
        var initialColumnName = scanner.next();

        var initialColumn = createColumn(initialColumnName, BoardColumnType.INITIAL, 0);
        columns.add(initialColumn);

        // Colunas extras
        for (int i = 1; i <= extraColumnCount; i++)
        {
            System.out.println("Informe o nome da coluna de tarefa pendente.");
            var pendingColumnName = scanner.next();

            var pendingColumn = createColumn(pendingColumnName, BoardColumnType.PENDING, i);
            columns.add(pendingColumn);
        }

        // Coluna Final
        System.out.println("Informe o nome da coluna final.");
        var finalColumnName = scanner.next();

        var finalColumn = createColumn(finalColumnName, BoardColumnType.FINAL, extraColumnCount + 1);
        columns.add(finalColumn);

        // Coluna de Cancelamento
        System.out.println("Informe o nome da coluna de cancelamento.");
        var cancelColumnName = scanner.next();

        var cancelColumn = createColumn(cancelColumnName, BoardColumnType.CANCEL, extraColumnCount + 2);
        columns.add(cancelColumn);

        entity.setBoardColumns(columns);

        try (var connection = ConnectionConfig.getConnection())
        {
            var service = new BoardService(connection);
            service.insert(entity);
        }
    }

    private void selectBoard() throws SQLException
    {
        System.out.println("Informe o id do board que deseja selecionar.");
        var id = scanner.nextLong();

        try (var connection = ConnectionConfig.getConnection())
        {
            var queryService = new BoardQueryService(connection);
            var optional = queryService.findById(id);
            optional.ifPresentOrElse(
                entity -> new BoardMenu(entity).execute(),
                () -> System.out.printf("Não foi encontrado um board de id %s.\n", id)
            );
        }
    }

    private void deleteBoard() throws SQLException
    {
        System.out.println("Informe o id do board que será excluído.");
        var id = scanner.nextLong();
        try (var connection = ConnectionConfig.getConnection())
        {
            var service = new BoardService(connection);
            var success = service.delete(id);
            if (success)
            {
                System.out.printf("O board de id %s foi excluído com sucesso.\n", id);
            }
            else
            {
                System.out.printf("Não foi encontrado um board de id %s.\n", id);
            }
        }
    }

    private BoardColumnEntity createColumn(final String name, final BoardColumnType kind, final int order)
    {
        var entity = new BoardColumnEntity();
        entity.setName(name);
        entity.setKind(kind);
        entity.setOrder(order);

        return entity;
    }
}
