package ver14.Model.Perft;

import ver14.SharedClasses.Game.moves.BasicMove;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class Perft {
    protected final Map<BasicMove, Long> map;

    private final int depth;

    public Perft(int depth) {
        this.depth = depth;
        map = new ConcurrentHashMap<>();
    }


    public void set(BasicMove move, Long numOfPos) {
        map.put(new BasicMove(move), numOfPos);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Perft perft) || perft.getSum() != getSum()) return false;

        return depth == perft.depth && getDifference(perft).isEmpty();
    }

    @Override
    public String toString() {
        return details() + "{" +
                "map=" + map +
                ", depth=" + depth +
                ", sum=" + getSum() +
                '}';
    }

    public long getSum() {
        AtomicLong sum = new AtomicLong();
        map.forEach((move, aLong) -> sum.addAndGet(aLong));
        return sum.get();
    }

    public Map<BasicMove, String> getDifference(Perft other) {
        Map<BasicMove, String> ret = new HashMap<>();

        String tS = "%-20s".formatted("Move");
        String format = tS + "%s\n%-20s %s\n%-20s %s";

        map.forEach((move, aLong) -> {
            if (!Objects.equals(other.fromMove(move), aLong)) {
                ret.put(move, format.formatted(move, details(), fromMove(move), other.details(), other.fromMove(move)));
            }
        });

        other.map.forEach((move, aLong) -> {
            if (!Objects.equals(fromMove(move), aLong)) {
                ret.put(move, format.formatted(move, details(), fromMove(move), other.details(), other.fromMove(move)));
            }
        });

        return ret;
    }

    public Long fromMove(BasicMove move) {
        return map.get(move);
    }

    public String details() {
        return "Mine";
    }

    public boolean contains(BasicMove move) {
        return map.containsKey(move);
    }


    public String shortStr() {
        return "depth: " + depth + " sum: " + getSum();
    }
}
