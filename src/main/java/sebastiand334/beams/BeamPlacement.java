package sebastiand334.beams;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public final class BeamPlacement {
    public final Vec3d start;
    public final Vec3d span;
    public final Vec3d direction;
    public final Vec3d cross1;
    public final Vec3d cross2;

    private static final Vec3d UP = new Vec3d(0.0, 1.0, 0.0);
    private static final Vec3d EAST = new Vec3d(1.0, 0.0, 0.0);

    public BeamPlacement(Vec3d start, Vec3d span) {
        this.start = start;
        this.span = span;
        this.direction = span.normalize();

        if (direction.x != 0.0 || direction.z != 0.0) {
            this.cross1 = UP.crossProduct(direction);
        } else {
            this.cross1 = EAST;
        }
        this.cross2 = direction.crossProduct(cross1);
    }

    public Vec3d getEnd() {
        return start.add(span);
    }

    public Vec3d corner1() {
        var offset = cross1.add(cross2).multiply(BEAM_RADIUS);
        return start.subtract(offset);
    }

    public Vec3d corner2() {
        var offset = cross1.add(cross2).multiply(BEAM_RADIUS);
        return getEnd().add(offset);
    }

    public Vec3d getShape() {
        var offset = cross1.add(cross2).multiply(BEAM_RADIUS * 2);
        return span.add(offset);
    }

    private static final double GRID_SIZE = 0.25d; // in blocks
    private static final double BEAM_RADIUS = 0.25d; // in blocks
    @Nullable
    private static Vec3d beamStart;

    public static double snapToGrid(double coord) {
        return Math.round(coord / GRID_SIZE) * GRID_SIZE;
    }

    public static Vec3d snapToGrid(Vec3d pos) {
        return new Vec3d(
            snapToGrid(pos.x),
            snapToGrid(pos.y),
            snapToGrid(pos.z)
        );
    }

    public static void startPlacingBeam(Vec3d start) {
        beamStart = snapToGrid(start);
    }

    public static void stopPlacingBeam() {
        beamStart = null;
    }

    public static @Nullable Vec3d getBeamStart() {
        return beamStart;
    }

    public static BeamPlacement target(Entity player) {
        var start = BeamPlacement.getBeamStart();
        if (start == null) {
            var target = player.raycast(40d, 0f, false);
            return new BeamPlacement(target.getPos(), UP);
        }

        var pos = player.getClientCameraPosVec(0f);
        var dir = player.getRotationVec(0f);

        var xDistance = (start.x - pos.x) / dir.x;
        var xDelta = pos.add(dir.multiply(xDistance)).subtract(start);
        Vec3d xCandidate = Math.abs(xDelta.y) > Math.abs(xDelta.z)
            ? new Vec3d(0d, snapToGrid(xDelta.y), 0d)
            : new Vec3d(0d, 0d, snapToGrid(xDelta.z));

        var yDistance = (start.y - pos.y) / dir.y;
        var yDelta = pos.add(dir.multiply(yDistance)).subtract(start);
        Vec3d yCandidate = Math.abs(yDelta.x) > Math.abs(yDelta.z)
            ? new Vec3d(snapToGrid(yDelta.x), 0d, 0d)
            : new Vec3d(0d, 0d, snapToGrid(yDelta.z));

        var zDistance = (start.z - pos.z) / dir.z;
        var zDelta = pos.add(dir.multiply(zDistance)).subtract(start);
        Vec3d zCandidate = Math.abs(zDelta.x) > Math.abs(zDelta.y)
            ? new Vec3d(snapToGrid(zDelta.x), 0d, 0d)
            : new Vec3d(0d, snapToGrid(zDelta.y), 0d);

        // find candidate that's closest to pos + d*dir
        var xScore = start.add(xCandidate).subtract(pos).normalize().dotProduct(dir);
        var yScore = start.add(yCandidate).subtract(pos).normalize().dotProduct(dir);
        var zScore = start.add(zCandidate).subtract(pos).normalize().dotProduct(dir);

        if (player.isSneaking()) {
            System.out.println("scores: " + xScore + ", " + yScore + ", " + zScore);
        }

        var span = xScore > yScore
            ? xScore > zScore ? xCandidate : zCandidate
            : yScore > zScore ? yCandidate : zCandidate;

        return new BeamPlacement(start, span);
    }
}
