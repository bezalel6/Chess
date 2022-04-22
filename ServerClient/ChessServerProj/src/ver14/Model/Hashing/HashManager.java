package ver14.Model.Hashing;//package ver14.Model.hashing;
//
//
//import ver14.Model.hashing.my_hash_maps.MyHashMap;
//
//import java.util.ArrayList;
//import java.util.concurrent.ConcurrentHashMap;
//
//public class HashManager {
//
//    public static final ArrayList<MyHashMap<?>> allMaps = new ArrayList<>();
//    public static final boolean hashBoard = true;
//    public static final boolean enableZobrist = true;
//
//    public static final boolean canActivate = true;
//
//    public static void clearAll() {
//        allMaps.forEach(ConcurrentHashMap::clear);
//    }
//
//    public enum Size {
//        LARGE(64000000),
//        MEDIUM(32000000),
//        SMALL(1000000),
//        NOTHING(0),
//        DEFAULT(LARGE),
//
//        MOVE_GENERATOR(LARGE, false),
//        ATTACKED_SQUARES(LARGE, false),
//        TRANSPOSITIONS(LARGE, false),
//        BB_SET_LOCS(MEDIUM, false),
//        EVALUATIONS(MEDIUM, false),
//        GAME_OVER(SMALL, false);
//
//
//        public static final Size[] SIZES = {LARGE, MEDIUM, SMALL, NOTHING};
//        public static final Size[] WORKING_HASHES = {BB_SET_LOCS, EVALUATIONS, GAME_OVER};
//        public final int size;
//
//        Size(Size S) {
//            this(S.size);
//        }
//
//        Size(int size) {
//            this.size = size;
//        }
//
//        Size(Size S, boolean activate) {
//            this.size = (activate && canActivate) ? S.size : 0;
//        }
//    }
//}
