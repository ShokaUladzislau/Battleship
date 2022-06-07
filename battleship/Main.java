package battleship;

import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.Scanner;

import static battleship.Main.scanner;

public class Main {
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        Player player1 = new Player("Player 1");
        Player player2 = new Player("Player 2");

        player1.fillPlayerBoard();
        passTheMove();
        player2.fillPlayerBoard();

        int moveNumber = 0;
        while (true) {
            moveNumber++;
            passTheMove();
            if (moveNumber % 2 != 0) {
                makeShot(player1, player2);
            } else {
                makeShot(player2, player1);
            }
        }
    }

    private static void makeShot(Player attacker, Player receiver) {
        receiver.battleBoard.printBoard();
        System.out.println("---------------------");
        attacker.shipBoard.printBoard();
        System.out.println(attacker.name + ", it's your turn:");
        receiver.shipBoard.takeShot(receiver.shipBoard.getCoordinate(scanner.nextLine().toUpperCase(Locale.ROOT)), receiver.battleBoard);
    }

    private static void passTheMove() {
        System.out.println("Press Enter and pass the move to another player\n ...");
        scanner.nextLine();
        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
    }
}

class Player {
    String name;
    Board shipBoard = new Board();
    Board battleBoard = new Board();

    public Player(String name) {
        this.name = name;
    }

    public void fillPlayerBoard() {
        System.out.println(name + ", place your ships on the game field\n");
        shipBoard.printBoard();

        for (Ship shipType : Ship.values()) {
            System.out.printf("Enter the coordinates of the %s (%d cells):%n\n", shipType.getName(), shipType.getCells());
            boolean correct = false;
            while (!correct) {
                correct = shipBoard.placeShip(scanner.nextLine().toUpperCase(Locale.ROOT), shipType);
            }
            shipBoard.printBoard();
        }
    }
}

class Board {
    private final String[][] fields = new String[10][10];
    public Board() {
        for (String[] field : this.fields) Arrays.fill(field, Symbols.FOG_OF_WAR.getSymbol());
    }
    public void printBoard() {
        char line = 'A';
        System.out.println("  1 2 3 4 5 6 7 8 9 10");
        for (String[] field : this.fields) {
            System.out.print(line + " ");
            for (String s : field) {
                System.out.print(s + " ");
            }
            line++;
            System.out.println();
        }
    }

    public boolean placeShip(String coordinatesInput, Ship shipType) {
        int[][] coordinates = getCoordinates(coordinatesInput);

        if (!checkShipSize(coordinates, shipType)) {
            System.out.printf("\nError! Wrong length of the %s! Try again:%n\n", shipType.getName());
            return false;
        }
        if (!checkShipLocation(coordinates)) {
            System.out.println("\nError! Wrong ship location! Try again:\n");
            return false;
        }

        int startLine = Math.min(coordinates[0][0], coordinates[1][0]);
        int endLine = Math.max(coordinates[0][0], coordinates[1][0]);
        int startColumn = Math.min(coordinates[0][1], coordinates[1][1]);
        int endColumn = Math.max(coordinates[0][1], coordinates[1][1]);

        int tempStartLine = Math.max(startLine, 1);
        int tempEndLine = Math.min(endLine, 8);
        int tempStartColumn = Math.max(startColumn, 1);
        int tempEndColumn = Math.min(endColumn, 8);

        for (int i = tempStartLine; i <= tempEndLine; i++) {
            for (int j = tempStartColumn; j <= tempEndColumn; j++) {
                if (this.fields[i][j - 1].equals(Symbols.SHIP.getSymbol()) ||
                        this.fields[i][j + 1].equals(Symbols.SHIP.getSymbol()) ||
                        this.fields[i - 1][j].equals(Symbols.SHIP.getSymbol()) ||
                        this.fields[i + 1][j].equals(Symbols.SHIP.getSymbol()) ||
                        this.fields[i - 1][j - 1].equals(Symbols.SHIP.getSymbol()) ||
                        this.fields[i + 1][j - 1].equals(Symbols.SHIP.getSymbol()) ||
                        this.fields[i - 1][j + 1].equals(Symbols.SHIP.getSymbol()) ||
                        this.fields[i + 1][j + 1].equals(Symbols.SHIP.getSymbol())) {
                    System.out.println("\nError! You placed it too close to another one. Try again:\n");
                    return false;
                }
            }
        }

        for (int i = startLine; i <= endLine; i++) {
            for (int j = startColumn; j <= endColumn; j++) {
                this.fields[i][j] = Symbols.SHIP.getSymbol();
            }
        }
        return true;
    }

    public boolean checkShipLocation(int[][] coordinates) {
        int startLine = Math.min(coordinates[0][0], coordinates[1][0]);
        int endLine = Math.max(coordinates[0][0], coordinates[1][0]);
        int startColumn = Math.min(coordinates[0][1], coordinates[1][1]);
        int endColumn = Math.max(coordinates[0][1], coordinates[1][1]);
        return startLine == endLine || startColumn == endColumn;
    }

    public boolean checkShipSize(int[][] coordinates, Ship shipType) {
        int startLine = Math.min(coordinates[0][0], coordinates[1][0]);
        int endLine = Math.max(coordinates[0][0], coordinates[1][0]);
        int startColumn = Math.min(coordinates[0][1], coordinates[1][1]);
        int endColumn = Math.max(coordinates[0][1], coordinates[1][1]);
        return Math.abs(endLine - startLine + 1) == shipType.getCells() || Math.abs(endColumn - startColumn + 1) == shipType.getCells();
    }

    public int[][] getCoordinates(String coordinates) {
        String[] dividedCoordinates = coordinates.split(" ");
        String firstCoordinateString = dividedCoordinates[0];
        String secondCoordinateString = dividedCoordinates[1];
        int[] firstCoordinate = new int[]{firstCoordinateString.charAt(0) - 65, Integer.parseInt(firstCoordinateString.substring(1)) - 1};
        int[] secondCoordinate = new int[]{secondCoordinateString.charAt(0) - 65, Integer.parseInt(secondCoordinateString.substring(1)) - 1};
        return new int[][]{firstCoordinate, secondCoordinate};
    }

    public int[] getCoordinate(String coordinate) {
        return new int[]{coordinate.charAt(0) - 65, Integer.parseInt(coordinate.substring(1)) - 1};
    }

    public void takeShot(int[] shotCoordinate, Board battleBoard) {
        if (shotCoordinate[0] < 0 || shotCoordinate[0] >= 10 || shotCoordinate[1] < 0 || shotCoordinate[1] >= 10) {
            System.out.println("Error! You entered the wrong coordinates! Try again:\n");
        } else {
            if (Objects.equals(this.fields[shotCoordinate[0]][shotCoordinate[1]], Symbols.FOG_OF_WAR.getSymbol()) || Objects.equals(this.fields[shotCoordinate[0]][shotCoordinate[1]], Symbols.MISS.getSymbol())) {
                this.fields[shotCoordinate[0]][shotCoordinate[1]] = Symbols.MISS.getSymbol();
                battleBoard.getShots(this);
                System.out.println("You missed!\n");
                return;
            }
            if (Objects.equals(this.fields[shotCoordinate[0]][shotCoordinate[1]], Symbols.SHIP.getSymbol()) || Objects.equals(this.fields[shotCoordinate[0]][shotCoordinate[1]], Symbols.HIT.getSymbol())) {
                this.fields[shotCoordinate[0]][shotCoordinate[1]] = Symbols.HIT.getSymbol();
                battleBoard.getShots(this);
                if (allShipsSank(this)) {
                    System.exit(0);
                    return;
                }
                if (this.shipIsSunk(shotCoordinate)) {
                    System.out.println("You sank a ship! Specify a new target:\n");
                } else {
                    System.out.println("You hit a ship!\n");
                }
            }
        }
    }

    private boolean shipIsSunk(int[] shotCoordinate) {
        boolean north = false;
        boolean east = false;
        boolean south = false;
        boolean west = false;

        for (int i = shotCoordinate[0]; i <= 9; i++) {
            if (i == 9 && Objects.equals(this.fields[i][shotCoordinate[1]], Symbols.HIT.getSymbol())) {
                south = true;
                break;
            }
            if (i == 9) break;
            if (Objects.equals(this.fields[i + 1][shotCoordinate[1]], Symbols.FOG_OF_WAR.getSymbol()) || Objects.equals(this.fields[i + 1][shotCoordinate[1]], Symbols.MISS.getSymbol())) {
                south = true;
                break;
            }
            if (Objects.equals(this.fields[i + 1][shotCoordinate[1]], Symbols.SHIP.getSymbol())) break;
        }

        for (int i = shotCoordinate[0]; i >= 0; i--) {
            if (i == 0 && Objects.equals(this.fields[i][shotCoordinate[1]], Symbols.HIT.getSymbol())) {
                north = true;
                break;
            }
            if (i == 0) break;
            if (Objects.equals(this.fields[i - 1][shotCoordinate[1]], Symbols.FOG_OF_WAR.getSymbol()) || Objects.equals(this.fields[i - 1][shotCoordinate[1]], Symbols.MISS.getSymbol())) {
                north = true;
                break;
            }
            if (Objects.equals(this.fields[i - 1][shotCoordinate[1]], Symbols.SHIP.getSymbol())) break;
        }

        for (int i = shotCoordinate[1]; i >= 0; i--) {
            if (i == 0 && Objects.equals(this.fields[shotCoordinate[0]][i], Symbols.HIT.getSymbol())) {
                west = true;
                break;
            }
            if (i == 0) break;
            if (Objects.equals(this.fields[shotCoordinate[0]][i - 1], Symbols.FOG_OF_WAR.getSymbol()) || Objects.equals(this.fields[shotCoordinate[0]][i - 1], Symbols.MISS.getSymbol())) {
                west = true;
                break;
            }
            if (Objects.equals(this.fields[shotCoordinate[0]][i - 1], Symbols.SHIP.getSymbol())) break;
        }

        for (int i = shotCoordinate[1]; i <= 9; i++) {
            if (i == 9 && Objects.equals(this.fields[shotCoordinate[0]][i], Symbols.HIT.getSymbol())) {
                east = true;
                break;
            }
            if (i == 9) break;
            if (Objects.equals(this.fields[shotCoordinate[0]][i + 1], Symbols.FOG_OF_WAR.getSymbol()) || Objects.equals(this.fields[shotCoordinate[0]][i + 1], Symbols.MISS.getSymbol())) {
                east = true;
                break;
            }
            if (Objects.equals(this.fields[shotCoordinate[0]][i + 1], Symbols.SHIP.getSymbol())) break;
        }

        return west && east && north && south;
    }

    public void getShots(Board sourceBoard) {
        for (int i = 0; i < sourceBoard.fields.length; i++) {
            for (int j = 0; j < sourceBoard.fields[i].length; j++) {
                if (Objects.equals(sourceBoard.fields[i][j], Symbols.HIT.getSymbol()) || Objects.equals(sourceBoard.fields[i][j], Symbols.MISS.getSymbol())) {
                    this.fields[i][j] = sourceBoard.fields[i][j];
                }
            }
        }
    }

    public boolean allShipsSank(Board shipboard) {
        boolean allShipsSank = true;

        for (String[] symbols : shipboard.fields) {
            for (String symbol : symbols) {
                if (Objects.equals(symbol, Symbols.SHIP.getSymbol())) {
                    allShipsSank = false;
                    break;
                }
            }
        }

        if (allShipsSank) System.out.println("You sank the last ship. You won. Congratulations!");
        return allShipsSank;
    }
}

enum Ship {
    AIRCRAFT_CARRIER(5, "Aircraft Carrier"), BATTLESHIP(4, "Battleship"), SUBMARINE(3, "Submarine"), CRUISER(3, "Cruiser"), DESTROYER(2, "Destroyer");
    final int cells;
    final String name;

    Ship(int cells, String name) {
        this.cells = cells;
        this.name = name;
    }

    public int getCells() {
        return cells;
    }

    public String getName() {
        return name;
    }
}

enum Symbols {
    FOG_OF_WAR("~"), SHIP("O"), HIT("X"), MISS("M");
    final String symbol;

    Symbols(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
}
