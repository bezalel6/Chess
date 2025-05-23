package ver24_flip_board;

import java.io.*;
import java.util.ArrayList;

public class Positions {
    private static final String storageRoot = "src/Assets/GameInfo/";
    private static final String positionsStoragePath = storageRoot + "Positions/";
    private static ArrayList<Position> allPositions = readAllPositions();
    private static final String positionsExtensions = ".pos";

    public static ArrayList<Position> getAllPositions() {
        return allPositions;
    }

    public static void main(String[] args) {
        writePosition("Starting Position", "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
        writePosition("Hanging Pieces test", "rnb1kbnr/pppppppp/8/8/8/3q4/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
        allPositions = readAllPositions();
        System.out.println(allPositions);
    }

    public static int addNewPosition(String name, String fen) {
        if (positionExists(name, fen))
            return -1;
        Position newPosition = new Position(name, fen);
        writePosition(newPosition);
        allPositions.add(newPosition);
        return allPositions.size() - 1;
    }

    public static int getIndexOfFen(String fen) {
        for (int i = 0, allPositionsSize = allPositions.size(); i < allPositionsSize; i++) {
            Position position = allPositions.get(i);
            if (position.fen.equals(fen)) {
                return i;
            }
        }
        return -1;
    }

    public static boolean positionExists(String fen) {
        return positionExists("", fen);
    }

    public static boolean positionExists(String name, String fen) {
        return getIndexOfFen(fen) != -1;
    }

    private static void writePosition(String name, String fen) {
        writePosition(new Position(name, fen));
    }

    private static void writePosition(Position position) {
        try (FileOutputStream fos = new FileOutputStream(positionsStoragePath + position.name + positionsExtensions);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(position);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static File[] getAllFiles(String dirPath) {
        File dir = new File(dirPath);
        File[] files = dir.listFiles();
        assert files != null;
        return files;
    }

    private static ArrayList<Position> readAllPositions() {
        ArrayList<Position> ret = new ArrayList<>();
        for (File file : getAllFiles(positionsStoragePath)) {
            Position position = readPosition(file.getName());
            assert position != null;
            ret.add(position);
            if (position.name.equals("Starting Position"))
                putInFirstSpot(ret, ret.indexOf(position));
        }
        return ret;
    }

    private static void putInFirstSpot(ArrayList<Position> list, int indexToBeFirst) {
        Position toBe = list.get(indexToBeFirst);
        Position currently = list.get(0);
        if (toBe == currently)
            return;
        list.set(0, toBe);
        list.set(indexToBeFirst, currently);
    }

    private static Position readPosition(String posName) {
        try (FileInputStream fis = new FileInputStream(positionsStoragePath + posName);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            Position pos = (Position) ois.readObject();
            return pos;
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static class Position implements Serializable {
        private static final long serialVersionUID = 1L;
        private final String name, fen;

        public Position(String name, String fen) {
            this.name = name;
            this.fen = fen;
        }

        public String getName() {
            return name;
        }

        public String getFen() {
            return fen;
        }

        @Override
        public String toString() {
            return "Position{" +
                    "name='" + name + '\'' +
                    ", fen='" + fen + '\'' +
                    '}';
        }
    }
}
