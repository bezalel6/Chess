package ver7.model_classes;

import ver7.SharedClasses.Location;

public class Attack {
    private Bitboard actualAttack = null;
    private Bitboard xRayAttack = null;
    private Bitboard checkRay = null;

    public Bitboard getCheckRay() {
        return checkRay;
    }

    public void setCheckRay(Bitboard checkRay) {
        this.checkRay = checkRay.cp();
    }

    public void setActualAttack(Bitboard actualAttack) {
        this.actualAttack = actualAttack.cp();
    }

    public Bitboard getxRayAttack() {
        return xRayAttack;
    }

    public void setxRayAttack(Bitboard xRayAttack) {
        this.xRayAttack = xRayAttack.cp();
    }

    public void prettyPrint() {
        StringBuilder stringBuilder = new StringBuilder();
        String RESET = "\033[0m";
        String RED = "\033[0;31m";
        String BLUE = "\u001B[34m";
        for (int i = 0; i < Location.NUM_OF_SQUARES; i++) {
            stringBuilder.append("|");
            String s = "0";
            Location loc = Location.getLoc(i);
            if (isActual(loc)) {
                s = RED + "1" + RESET;
            } else if (isXray(loc)) {
                s = BLUE + "2" + RESET;
            }
            stringBuilder.append(s).append("| ");
            if ((i + 1) % 8 == 0)
                stringBuilder.append("\n");
        }
        System.out.println("\n" + stringBuilder);
    }

    public boolean isActual(Location loc) {
        return actualAttack != null && actualAttack.isSet(loc);
    }

    public boolean isXray(Location loc) {
        return xRayAttack != null && xRayAttack.isSet(loc);
    }

    public boolean isCheck(Location loc) {
        return checkRay != null && checkRay.isSet(loc);
    }

    public Bitboard both() {
        return xRayAttack.and(actualAttack);
    }

    public boolean anyMatch(Location loc) {
        return isCheck(loc) || isActual(loc) || isXray(loc);
    }
}
