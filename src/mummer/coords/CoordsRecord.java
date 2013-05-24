package mummer.coords;

public class CoordsRecord {

    private Coord[] coords;
    private double idy;

    public CoordsRecord(Coord c1, Coord c2, double idy) {
        coords = new Coord[2];
        coords[0] = c1;
        coords[1] = c2;
        c1.setCoordsRecord(this);
        c2.setCoordsRecord(this);
        this.idy = idy;
    }

    public Coord getCoord(int index) {
        return coords[index];
    }

    public Coord[] getCoords() {
        return coords;
    }

    public Coord getOther(Coord c) {
        return (coords[0].equals(c)) ? coords[1] : coords[0];
    }
}
