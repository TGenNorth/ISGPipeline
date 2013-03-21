package org.tgen.commons.feature;

import org.tgen.commons.coords.CoordsRecord;

public class Coord extends Locus {

    private int length;
    private CoordsRecord coordsRecord;
    private boolean reversed;

    public Coord(String name, int start, int end, int length, boolean reversed) {
        super(name, start, end);
        this.length = length;
        this.reversed = reversed;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void setCoordsRecord(CoordsRecord coordsRecord) {
        this.coordsRecord = coordsRecord;
    }

    public CoordsRecord getCoordsRecord() {
        return coordsRecord;
    }

    public boolean isReversed() {
        return reversed;
    }

    public boolean contains(int rPos) {
        if (rPos >= getStart() && rPos <= getEnd()) {
            return true;
        }
        return false;
    }

    public Coord fixLength() {
        Coord other = coordsRecord.getOther(this);
        if (other.getLength() == length) {
            return this;
        }
        
        int start = getStart();
        int end = getEnd();
        int oStart = other.getStart();
        int oEnd = other.getEnd();
        
        int diff = Math.abs(other.getLength() - length);
        if (length > other.getLength()) {
            if (reversed) {
                start += diff;
            } else {
                end -= diff;
            }
        } else {
            oEnd -= diff;
        }


        Coord ret = new Coord(getChr(), start, end, length, reversed);
        Coord ret2 = new Coord(other.getChr(), oStart, oEnd, length, other.reversed);
        ret.setCoordsRecord(new CoordsRecord(ret, ret2, 0));
        return ret;
    }
    
    public Coord intersects(Coord c) {
        return intersects(c.getChr(), c.getStart(), c.getEnd());
    }

    public Coord intersects(String chr, int cStart, int cEnd) {
        if (!chr.equals(getChr())) {
            throw new IllegalArgumentException("Coord chromosome mismatch");
        }
        Coord other = coordsRecord.getOther(this);
        int start = getStart();
        int end = getEnd();
        int oStart = other.getStart();
        int oEnd = other.getEnd();
        
        int offsetLeft = cStart - start;
        int offsetRight = end - cEnd;
        if (offsetLeft < 0) {
            offsetLeft = 0;
        }
        if (offsetRight < 0) {
            offsetRight = 0;
        }
        int length = (end - offsetRight) - (start + offsetLeft);
        Coord ret = new Coord(getChr(), start + offsetLeft, end - offsetRight, length, reversed);

//        System.out.println(offsetLeft+" "+offsetRight);
//        System.out.println(length);
        
        if (reversed) {
            int tmp = offsetLeft;
            offsetLeft = offsetRight;
            offsetRight = tmp;
        }
        Coord ret2 = new Coord(other.getChr(), oStart + offsetLeft, oEnd - offsetRight, length, other.reversed);
        ret.setCoordsRecord(new CoordsRecord(ret, ret2, 0));
        return ret;
    }
}
