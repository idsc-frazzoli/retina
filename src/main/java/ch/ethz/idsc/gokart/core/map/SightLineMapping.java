package ch.ethz.idsc.gokart.core.map;

import ch.ethz.idsc.gokart.core.fuse.SafetyConfig;
import ch.ethz.idsc.gokart.core.perc.SpacialXZObstaclePredicate;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmClient;
import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
import ch.ethz.idsc.gokart.lcm.lidar.Vlp16LcmHandler;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.lidar.LidarPolarFiringCollector;
import ch.ethz.idsc.retina.lidar.LidarSectorProvider;
import ch.ethz.idsc.retina.lidar.VelodyneStatics;
import ch.ethz.idsc.retina.lidar.vlp16.Vlp16PolarProvider;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

import java.awt.*;
import java.util.Collection;

public class SightLineMapping extends AbstractSightLines implements OccupancyGrid {
    // TODO check rationale behind constant 10000!
    private static final int LIDAR_SAMPLES = 10000;
    private static final int SECTORS = 72;
    // ---
    private final LidarPolarFiringCollector lidarPolarFiringCollector = //
            new LidarPolarFiringCollector(LIDAR_SAMPLES, 3);
    private final Vlp16PolarProvider lidarPolarProvider = new Vlp16PolarProvider();
    private final LidarSectorProvider lidarSectorProvider = //
            new LidarSectorProvider(VelodyneStatics.AZIMUTH_RESOLUTION, SECTORS);
    private final GokartPoseLcmClient gokartPoseLcmClient = new GokartPoseLcmClient();
    private final Vlp16LcmHandler vlp16LcmHandler = SensorsConfig.GLOBAL.vlp16LcmHandler();
    private final SightLineOccupancyGrid occupancyGrid = MappingConfig.GLOBAL.createSightLineOccupancyGrid();
    // ---
    private boolean isLaunched = true;
    private final int waitMillis;

    public static SightLineMapping defaultGokart() {
        SightLineMapping sightLineMapping = //
                new SightLineMapping(SafetyConfig.GLOBAL.createSpacialXZObstaclePredicate(),200);
        sightLineMapping.addBlindSpot(Tensors.vector(3., 3.4));
        sightLineMapping.addBlindSpot(Tensors.vector(6.1, 0.2));
        return sightLineMapping;
    }

    public SightLineMapping(SpacialXZObstaclePredicate predicate, int waitMillis) {
        super(predicate);
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
        super.getEvent(getEvent);
        occupancyGrid.setPose(getEvent.getPose());
    }

    @Override // from Runnable
    public void run() {
        while (isLaunched) {
            Collection<Tensor> points = getClosestPoints();
            if (!points.isEmpty()) {
                Tensor polygon = polygon(points);
                closeSector(polygon);
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

    @Override // from RenderInterface
    public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
        occupancyGrid.render(geometricLayer, graphics);
    }

}
