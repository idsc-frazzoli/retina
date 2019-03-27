// code by ynager, mheim, gjoel
package ch.ethz.idsc.gokart.core.map;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmClient;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
import ch.ethz.idsc.gokart.lcm.lidar.Vlp16LcmHandler;
import ch.ethz.idsc.retina.lidar.LidarRayBlockEvent;
import ch.ethz.idsc.retina.lidar.LidarRayBlockListener;
import ch.ethz.idsc.retina.util.StartAndStoppable;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

import java.nio.FloatBuffer;

/* package */ abstract class AbstractLidarProcessor implements //
        StartAndStoppable, LidarRayBlockListener, GokartPoseListener, Runnable {
    // TODO check rationale behind constant 10000!
    protected static final int LIDAR_SAMPLES = 10000;
    // ---
    private final Thread thread = new Thread(this);
    protected boolean isLaunched = true;
    protected final int waitMillis;
    // ---
    private final GokartPoseLcmClient gokartPoseLcmClient = new GokartPoseLcmClient();
    protected final Vlp16LcmHandler vlp16LcmHandler = SensorsConfig.GLOBAL.vlp16LcmHandler();
    /** points_ferry is null or a matrix with dimension Nx3
     * containing the cross-section of the static geometry
     * with the horizontal plane at height of the lidar */
    protected Tensor points_ferry = null;
    protected GokartPoseEvent gokartPoseEvent;

    public AbstractLidarProcessor(int waitMillis) {
        this.waitMillis = waitMillis;
        gokartPoseLcmClient.addListener(this);
    }

    @Override // from StartAndStoppable
    public void start() {
        vlp16LcmHandler.startSubscriptions();
        gokartPoseLcmClient.startSubscriptions();
        thread.start();
    }

    @Override // from StartAndStoppable
    public void stop() {
        isLaunched = false;
        thread.interrupt();
        vlp16LcmHandler.stopSubscriptions();
        gokartPoseLcmClient.stopSubscriptions();
    }

    @Override // from LidarRayBlockListener
    public void lidarRayBlock(LidarRayBlockEvent lidarRayBlockEvent) {
        if (lidarRayBlockEvent.dimensions != 3)
            throw new RuntimeException("dim=" + lidarRayBlockEvent.dimensions);
        // ---
        FloatBuffer floatBuffer = lidarRayBlockEvent.floatBuffer;
        points_ferry = Tensors.vector(i -> Tensors.of( //
                DoubleScalar.of(floatBuffer.get()), //
                DoubleScalar.of(floatBuffer.get()), //
                DoubleScalar.of(floatBuffer.get())), lidarRayBlockEvent.size());
        thread.interrupt();
    }

    @Override // from GokartPoseListener
    public void getEvent(GokartPoseEvent gokartPoseEvent) {
        this.gokartPoseEvent = gokartPoseEvent;
    }
}
