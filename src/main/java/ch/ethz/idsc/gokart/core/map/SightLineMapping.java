// code by gjoel
package ch.ethz.idsc.gokart.core.map;

import ch.ethz.idsc.gokart.core.fuse.SafetyConfig;
import ch.ethz.idsc.gokart.core.perc.SpacialXZObstaclePredicate;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.lidar.*;
import ch.ethz.idsc.retina.lidar.vlp16.Vlp16PolarProvider;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Tensor;

import java.awt.*;
import java.util.Collection;

/** create an obstacle map based on lidar sight lines */
public class SightLineMapping extends AbstractLidarProcessor implements RenderInterface, OccupancyGrid {
    private final LidarPolarFiringCollector lidarPolarFiringCollector = //
            new LidarPolarFiringCollector(LIDAR_SAMPLES, 3);
    private final Vlp16PolarProvider lidarPolarProvider = new Vlp16PolarProvider();
    private final LidarSectorProvider lidarSectorProvider = //
            new LidarSectorProvider(VelodyneStatics.AZIMUTH_RESOLUTION, SightLineHandler.SECTORS);
    private final SightLineOccupancyGrid occupancyGrid = MappingConfig.GLOBAL.createSightLineOccupancyGrid();
    private final ErodedMap map = ErodedMap.of(occupancyGrid, MappingConfig.GLOBAL.obsRadius);
    // -----------------------------------------------------------------------------------------------------------------
    private final BlindSpots blindSpots;
    protected final SpacialXZObstaclePredicate predicate;
    // -----------------------------------------------------------------------------------------------------------------

    public static SightLineMapping defaultGokart() {
        return new SightLineMapping(SafetyConfig.GLOBAL.createSpacialXZObstaclePredicate(), BlindSpots.defaultGokart(),200);
    }

    public SightLineMapping(SpacialXZObstaclePredicate predicate, BlindSpots blindSpots, int waitMillis) {
        super(waitMillis);
        this.predicate = predicate;
        this.blindSpots = blindSpots;
        // ---
        lidarPolarProvider.setLimitLo(Magnitude.METER.toDouble(MappingConfig.GLOBAL.minDistance));
        lidarPolarProvider.addListener(lidarPolarFiringCollector);
        lidarSectorProvider.addListener(lidarPolarFiringCollector);
        lidarPolarFiringCollector.addListener(this);
        vlp16LcmHandler.velodyneDecoder.addRayListener(lidarPolarProvider);
        vlp16LcmHandler.velodyneDecoder.addRayListener(lidarSectorProvider);
    }

    @Override // from OccupancyGrid
    public Tensor getGridSize() {
        return occupancyGrid.getGridSize();
    }

    @Override // from OccupancyGrid
    public boolean isCellOccupied(int pix, int piy) {
        return occupancyGrid.isCellOccupied(pix, piy);
    }

    @Override // from OccupancyGrid
    public Tensor getTransform() {
        return occupancyGrid.getTransform();
    }

    @Override // from OccupancyGrid
    public void clearStart(int startX, int startY, double orientation) {
        occupancyGrid.clearStart(startX, startY, orientation);
    }

    @Override // from OccupancyGrid
    public boolean isMember(Tensor state) {
        return occupancyGrid.isMember(state);
    }

    @Override // from GokartPoseListener
    public void getEvent(GokartPoseEvent gokartPoseEvent) {
        super.getEvent(gokartPoseEvent);
        occupancyGrid.setPose(gokartPoseEvent.getPose());
        map.setPose(gokartPoseEvent.getPose());
    }

    @Override // from Runnable
    public void run() {
        while (isLaunched) {
            Collection<Tensor> points = SightLineHandler.getClosestPoints(points_ferry, predicate, blindSpots);
            if (!points.isEmpty()) {
                Tensor polygon = SightLineHandler.polygon(points);
                SightLineHandler.closeSector(polygon);
                occupancyGrid.updateMap(polygon);
            } else {
                try {
                    Thread.sleep(waitMillis);
                } catch (Exception e) {
                    // ---
                }
            }
        }
    }

    public void prepareMap() {
        map.genObstacleMap();
    }

    public ImageGrid getMap() {
        return map;
    }

    @Override // from RenderInterface
    public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
        occupancyGrid.render(geometricLayer, graphics);
    }

}
