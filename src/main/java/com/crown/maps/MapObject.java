package com.crown.maps;

import com.crown.common.NamedObject;
import com.crown.common.utils.Random;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.tuple.Pair;

public abstract class MapObject extends NamedObject {
    protected Map map;

    private final MapIcon<?> mapIcon;
    private final MapWeight mapWeight;

    protected Point3D[] lastParticles;
    protected Point3D[] particles;

    /**
     * Creates new map object with size of 1
     * on the random map point.
     */
    public MapObject(
        String name,
        Map map,
        MapIcon<?> mapIcon,
        MapWeight mapWeight
    ) {
        this(
            name,
            map,
            mapIcon,
            mapWeight,
            Random.getPoint(map)
        );
    }

    /**
     * Creates new object with size of 1.
     */
    public MapObject(
        String name,
        Map map,
        MapIcon<?> mapIcon,
        MapWeight mapWeight,
        Point3D pt0
    ) {
        this(
            name,
            map,
            mapIcon,
            mapWeight,
            new Point3D[] { pt0 }
        );
    }

    /**
     * Creates new "large" object placed
     * on multiple points of map.
     */
    public MapObject(
        String name,
        Map map,
        MapIcon<?> mapIcon,
        MapWeight mapWeight,
        Point3D[] particles
    ) {
        super(name);
        this.map = map;
        this.mapIcon = mapIcon;
        this.mapWeight = mapWeight;
        this.particles = lastParticles = particles;
    }

    public Map getMap() {
        return map;
    }

    public MapIcon<?> getMapIcon() {
        return this.mapIcon;
    }

    public MapWeight getMapWeight() {
        return mapWeight;
    }

    public int getWidth() {
        var bounds = getBounds();
        return bounds.getRight().x - bounds.getLeft().x + 1;
    }

    public int getHeight() {
        var bounds = getBounds();
        return bounds.getRight().y - bounds.getLeft().y + 1;
    }

    /**
     * Returns pair of min & max points of this map object.
     */
    private Pair<Point3D, Point3D> getBounds() {
        var minPt = new Point3D(map.xSize, map.ySize, map.zSize);
        var maxPt = new Point3D();
        for (var part : particles) {
            minPt = minPt.min(part);
            maxPt = maxPt.max(part);
        }
        return Pair.of(minPt, maxPt);
    }

    public Point3D getPt0() {
        return particles[0];
    }

    public Point3D getLastPt0() {
        return lastParticles[0];
    }

    /**
     * Moves object's view points to a new map location.
     * Unsafe, map bounds are not checked.
     */
    public void moveView(int deltaX, int deltaY, int deltaZ) {
        if (deltaX > 0) {
            mapIcon.flipped = true;
        } else if (deltaX < 0) {
            mapIcon.flipped = false;
        }
        lastParticles = SerializationUtils.clone(particles);
        for (Point3D part : particles) {
            part.x += deltaX;
            part.y += deltaY;
            part.z += deltaZ;
        }
        map.move(this);
    }

    @Override
    public String toString() {
        // noinspection HardCodedStringLiteral
        return getName()
               + " [#" + getId()
               + " | " + getMapIcon()
               + " | w=" + getMapWeight()
               + " | @ " + getPt0()
               + " map #" + getMap().getId()
               + "]";
    }
}
