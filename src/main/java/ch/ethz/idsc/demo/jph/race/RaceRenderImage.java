// code by jph
package ch.ethz.idsc.demo.jph.race;

import java.awt.Color;
import java.awt.Graphics2D;
import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.lcm.OfflineLogListener;
import ch.ethz.idsc.gokart.offline.channel.GokartPoseChannel;
import ch.ethz.idsc.gokart.offline.video.DriftLinesRender;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Max;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Round;

/** used in analysis of race on 20190701 between human driver and dynamic mpc
 * 
 * https://github.com/idsc-frazzoli/retina/files/3492127/20190812_autonomous_human_racing.pdf */
/* package */ class RaceRenderImage implements OfflineLogListener, RenderInterface {
  private static final Scalar MARGIN = Quantity.of(0.1, SI.SECOND);
  // ---
  private final Scalar duration;
  private final Clip clip;
  private final DriftLinesRender driftLinesRender;
  private Scalar maxVx = Quantity.of(0, SI.VELOCITY);
  private Scalar maxVy = Quantity.of(0, SI.VELOCITY);
  private Scalar maxVw = Quantity.of(0, SI.PER_SECOND);

  public RaceRenderImage(Scalar time0, Scalar duration, Color color) {
    this.duration = duration;
    clip = Clips.interval(time0.subtract(MARGIN), time0.add(duration).add(MARGIN));
    driftLinesRender = new DriftLinesRender(100_000, color, 1);
  }

  @Override // from OfflineLogListener
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (clip.isInside(time))
      if (channel.equals(GokartPoseChannel.INSTANCE.channel())) {
        GokartPoseEvent gokartPoseEvent = GokartPoseEvent.of(byteBuffer);
        driftLinesRender.getEvent(gokartPoseEvent);
        Tensor velocity = gokartPoseEvent.getVelocity();
        maxVx = Max.of(maxVx, velocity.Get(0));
        maxVy = Max.of(maxVy, velocity.Get(1).abs());
        maxVw = Max.of(maxVw, velocity.Get(2).abs());
      }
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    driftLinesRender.render(geometricLayer, graphics);
  }

  public Scalar duration() {
    return Round._3.apply(duration);
  }

  public Scalar maxVx() {
    return Round._2.apply(maxVx);
  }

  public Scalar maxVy() {
    return Round._2.apply(maxVy);
  }

  public Scalar maxVw() {
    return Round._2.apply(maxVw);
  }
}
