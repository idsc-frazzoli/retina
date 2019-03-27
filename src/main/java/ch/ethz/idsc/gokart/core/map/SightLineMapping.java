package ch.ethz.idsc.gokart.core.map;

import ch.ethz.idsc.gokart.core.fuse.SafetyConfig;
import ch.ethz.idsc.gokart.core.perc.SpacialXZObstaclePredicate;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmClient;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
import ch.ethz.idsc.gokart.lcm.lidar.Vlp16LcmHandler;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.lidar.*;
import ch.ethz.idsc.retina.lidar.vlp16.Vlp16PolarProvider;
import ch.ethz.idsc.retina.util.StartAndStoppable;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

import java.awt.*;
import java.nio.FloatBuffer;
import java.util.Collection;

/** create an obstacle map based on lidar sight lines */
public class SightLineMapping implements //
        StartAndStoppable, LidarRayBlockListener, GokartPoseListener, RenderInterface, Runnable, OccupancyGrid {
    // TODO check rationale behind constant 10000!
    private static final int LIDAR_SAMPLES = 10000;
    // ---
    private final LidarPolarFiringCollector lidarPolarFiringCollector = //
            new LidarPolarFiringCollector(LIDAR_SAMPLES, 3);
    private final Vlp16PolarProvider lidarPolarProvider = new Vlp16PolarProvider();
    private final LidarSectorProvider lidarSectorProvider = //
            new LidarSectorProvider(VelodyneStatics.AZIMUTH_RESOLUTION, SightLineHandler.SECTORS);
    private final GokartPoseLcmClient gokartPoseLcmClient = new GokartPoseLcmClient();
    private final Vlp16LcmHandler vlp16LcmHandler = SensorsConfig.GLOBAL.vlp16LcmHandler();
    private final SightLineOccupancyGrid occupancyGrid = MappingConfig.GLOBAL.createSightLineOccupancyGrid();
    private final ErodedMap map = ErodedMap.of(occupancyGrid, MappingConfig.GLOBAL.obsRadius);
    // ---
    private boolean isLaunched = true;
    private final int waitMillis;
    // -----------------------------------------------------------------------------------------------------------------
    /** pointsPolar_ferry is null or a matrix with dimension Nx3
     * containing the cross-section of the static geometry
     * with the horizontal plane at height of the lidar */
    private Tensor pointsPolar_ferry = null;
    private final BlindSpots blindSpots;
    // ---
    protected final Thread thread = new Thread(this);
    protected final SpacialXZObstaclePredicate predicate;
    protected GokartPoseEvent gokartPoseEvent;
    // -----------------------------------------------------------------------------------------------------------------

    public static SightLineMapping defaultGokart() {
        return new SightLineMapping(SafetyConfig.GLOBAL.createSpacialXZObstaclePredicate(), BlindSpots.defaultGokart(),200);
    }

    public SightLineMapping(SpacialXZObstaclePredicate predicate, BlindSpots blindSpots, int waitMillis) {
        this.predicate = predicate;
        this.blindSpots = blindSpots;
        this.waitMillis = waitMillis;
        // ---
        lidarPolarProvider.setLimitLo(Magnitude.METER.toDouble(MappingConfig.GLOBAL.minDistance));
        lidarPolarProvider.addListener(lidarPolarFiringCollector);
        lidarSectorProvider.addListener(lidarPolarFiringCollector);
        lidarPolarFiringCollector.addListener(this);
        gokartPoseLcmClient.addListener(this);
        vlp16LcmHandler.velodyneDecoder.addRayListener(lidarPolarProvider);
        vlp16LcmHandler.velodyneDecoder.addRayListener(lidarSectorProvider);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Override // from LidarRayBlockListener
    public void lidarRayBlock(LidarRayBlockEvent lidarRayBlockEvent) {
        if (lidarRayBlockEvent.dimensions != 3)
            throw new RuntimeException("dim=" + lidarRayBlockEvent.dimensions);
        // ---
        FloatBuffer floatBuffer = lidarRayBlockEvent.floatBuffer;
        pointsPolar_ferry = Tensors.vector(i -> Tensors.of( //
                DoubleScalar.of(floatBuffer.get()), //
                DoubleScalar.of(floatBuffer.get()), //
                DoubleScalar.of(floatBuffer.get())), lidarRayBlockEvent.size());
        thread.interrupt();
    }
    // -----------------------------------------------------------------------------------------------------------------

    @Override // from StartAndStoppable
    public void start() {
        vlp16LcmHandler.startSubscriptions();
        gokartPoseLcmClient.startSubscriptions();
        thread.start();
        isLaunched = true;
    }

    @Override // from StartAndStoppable
    public void stop() {
        isLaunched = false;
        thread.interrupt();
        vlp16LcmHandler.stopSubscriptions();
        gokartPoseLcmClient.stopSubscriptions();
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
    public void getEvent(GokartPoseEvent getEvent) {
        gokartPoseEvent = getEvent;;
        occupancyGrid.setPose(getEvent.getPose());
        map.setPose(getEvent.getPose());
    }

    @Override // from Runnable
    public void run() {
        while (isLaunched) {
            Collection<Tensor> points = SightLineHandler.getClosestPoints(pointsPolar_ferry, predicate, blindSpots);
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
