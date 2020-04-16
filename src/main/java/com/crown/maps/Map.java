package com.crown.maps;

import com.crown.common.NamedObject;
import org.jetbrains.annotations.NotNull;
import rlforj.IBoard;

public abstract class Map extends NamedObject implements IBoard {
    public final int xSize;
    public final int ySize;
    public final int zSize;

    protected final MapObject[][][] container;

    private final IMapIcon<?> mapEndIcon;

    public Map(
        String name,
        int xSize,
        int ySize,
        int zSize,
        IMapIcon<?> mapEndIcon
    ) {
        super(name);
        this.xSize = xSize;
        this.ySize = ySize;
        this.zSize = zSize;
        this.container = new MapObject[zSize][ySize][xSize];
        this.mapEndIcon = mapEndIcon;
    }

    /**
     * Returns 2D area for map region with given radius.
     */
    public IMapIcon<?>[][] get2DArea(Point3D pt, int radius) {
        final int diameter = radius * 2;
        IMapIcon<?>[][] area = new IMapIcon[diameter][diameter];

        int diffX = 0;
        int diffY = 0;
        if (pt.x - radius < 0) {
            diffX = Math.abs(pt.x - radius);
        }
        if (pt.y - radius < 0) {
            diffY = Math.abs(pt.y - radius);
        }

        for (int z = 0; z < diameter; z++) {
            for (int y = 0; y < diameter; y++) {
                for (int x = 0; x < diameter; x++) {
                    if (y > diffY - 1 && x > diffX - 1) {
                        MapObject o = get(pt.z, pt.y + y - radius, pt.x + x - radius);
                        if (o != null) {
                            area[y][x] = o.getMapIcon();
                        }
                    }
                }
            }
        }
        for (int y = 0; y < diameter; y++) {
            for (int x = 0; x < diameter; x++) {
                if (area[y][x] == null) {
                    area[y][x] = mapEndIcon;
                }
            }
        }

        return area;
    }

    public void add(MapObject o) {
        set(o.getPt(), o);
    }

    public void remove(@NotNull MapObject o) {
        set(o.getPt(), null);
    }

    public void move(@NotNull MapObject o) {
        if (contains(o.getPt())) {
            if (contains(o.getLastPt())) {
                set(o.getLastPt(), null);
            }
            add(o);
        }
    }

    public boolean contains(@NotNull Point3D pt) {
        return pt.x >= 0
               && pt.x <= xSize
               && pt.y >= 0
               && pt.y <= ySize
               && pt.z >= 0
               && pt.z <= zSize;
    }

    public MapObject get(@NotNull Point3D pt) {
        return get(pt.x, pt.y, pt.z);
    }

    public MapObject get(int x, int y, int z) {
        return container[z][y][x];
    }

    protected void set(@NotNull Point3D pt, MapObject value) {
        set(pt.x, pt.y, pt.z, value);
    }

    protected void set(int x, int y, int z, MapObject value) {
        container[z][y][x] = value;
    }

    protected void clear() {
        for (int z = 0; z < zSize; z++) {
            for (int y = 0; y < ySize; y++) {
                for (int x = 0; x < xSize; x++) {
                    set(x, y, z, null);
                }
            }
        }
    }

    /**
     * Used for player vision logic. Do not use it manually!
     * Use contains(x, y, z) instead.
     */
    @Override
    @Deprecated
    public boolean contains(int x, int y) {
        return x >= 0
               && x <= xSize
               && y >= 0
               && y <= ySize;
    }

    /**
     * Used for player vision logic. Do not use it manually!
     */
    @Override
    @Deprecated
    public boolean isObstacle(int x, int y) {
        for (int z = 0; z < zSize; z++) {
            if (get(x, y, z).getMapWeight() == MapWeight.OBSTACLE) {
                return true;
            }
        }
        return false;
    }

    /**
     * Used for player vision logic. Do not use it manually!
     */
    @Override
    @Deprecated
    public boolean blocksLight(int x, int y) {
        for (int z = 0; z < zSize; z++) {
            if (get(x, y, z).getMapWeight() == MapWeight.BLOCKS_LIGHT) {
                return true;
            }
        }
        return false;
    }

    /**
     * Used for player vision logic. Do not use it manually!
     */
    @Override
    @Deprecated
    public boolean blocksStep(int x, int y) {
        for (int z = 0; z < zSize; z++) {
            if (get(x, y, z).getMapWeight() == MapWeight.BLOCKS_STEP) {
                return true;
            }
        }
        return false;
    }

    /**
     * Used for player vision logic. Do not use it manually!
     */
    @Override
    @Deprecated
    public void visit(int x, int y) {
        // TODO: implement 'visit'
    }
}
