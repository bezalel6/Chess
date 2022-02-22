package ver2.types;


import ver2.Location;

public class test {
    public static void main(String args[]) {
        Annotation ann = new Annotation('e',4);
        System.out.println(ann.getMatLoc());
        ann.updateLoc(new Location());
        System.out.println(ann.getMatLoc());
    }
}