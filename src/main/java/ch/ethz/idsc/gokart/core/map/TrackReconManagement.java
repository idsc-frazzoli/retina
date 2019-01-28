// code by jph
package ch.ethz.idsc.gokart.core.map;

import java.awt.Graphics2D;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.gokart.core.map.TrackRefinement.TrackConstraint;
import ch.ethz.idsc.gokart.core.mpc.MPCBSplineTrack;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.gui.top.TrackRender;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Timing;
import ch.ethz.idsc.tensor.mat.LinearSolve;
import ch.ethz.idsc.tensor.qty.Quantity;

// TODO JPH/MH manage unused variables
public class TrackReconManagement implements RenderInterface {
  private static final Scalar RADIUS_OFFSET = Quantity.of(0.4, SI.METER);
  private static final Scalar SPACING = RealScalar.of(1.5); // TODO should be meters
  private static final Scalar CP_RESOLUTION = RealScalar.of(0.5);
  // ---
  private final TrackRender trackRender = new TrackRender();
  private final OccupancyGrid occupancyGrid;
  private final TrackLayoutInitialGuess initialGuess;
  private final TrackRefinement refinenement;
  private Tensor trackDataXYR = null;
  private int startX = -1;
  private int startY = -1;
  private int width = 0;
  private int heigth = 0;
  private int count = 0;
  private double startOrientation = 0;
  private MPCBSplineTrack lastTrack;
  private boolean closedTrack = false;
  private boolean oldWasClosed = false;
  private Timing lastTrackReset = Timing.started();
  private List<TrackConstraint> constraints = new LinkedList<>();
  private Scalar openTrackValid = Quantity.of(1, SI.SECOND);
  private Scalar timeSinceLastTrackUpdate = Quantity.of(0, SI.SECOND);
  private List<TrackConstraint> trackConstraints = null;
  private boolean startSet = false;

  public TrackReconManagement(OccupancyGrid occupancyGrid) {
    this.occupancyGrid = occupancyGrid;
    this.initialGuess = new TrackLayoutInitialGuess(occupancyGrid);
    this.refinenement = new TrackRefinement(occupancyGrid);
    Tensor gridSize = occupancyGrid.getGridSize();
    width = gridSize.Get(0).number().intValue();
    heigth = gridSize.Get(1).number().intValue();
  }

  // TODO JPH/MH function not used
  private void resetStart() {
    startSet = false;
  }

  public void resetTrack() {
    lastTrack = null;
    trackRender.setTrack(null);
  }

  public boolean isStartSet() {
    return startSet;
  }

  /** @param gokartPoseEvent non-null */
  public void setStart(GokartPoseEvent gokartPoseEvent) {
    setStart(gokartPoseEvent.getPose());
  }

  /** set start position
   * 
   * @param pose [x[m], y[m], angle] */
  private void setStart(Tensor pose) {
    Tensor transform = occupancyGrid.getTransform();
    Tensor hpos = Tensors.of(pose.Get(0), pose.Get(1), Quantity.of(1, SI.METER));
    Tensor pixelPos = LinearSolve.of(transform, hpos);
    startX = pixelPos.Get(0).number().intValue();
    startY = pixelPos.Get(1).number().intValue();
    startOrientation = pose.Get(2).number().doubleValue();
    if (startX >= 0 && startX < width && startY >= 0 && startY < heigth)
      startSet = true;
  }

  /** @param gokartPoseEvent non null
   * @param dTime
   * @return */
  // FIXME may return null!!!!!!
  public Optional<MPCBSplineTrack> update(GokartPoseEvent gokartPoseEvent, Scalar dTime) {
    return update(gokartPoseEvent.getPose(), dTime);
  }

  private Optional<MPCBSplineTrack> update(Tensor pose, Scalar dTime) {
    System.out.println("update called: " + timeSinceLastTrackUpdate);
    timeSinceLastTrackUpdate = timeSinceLastTrackUpdate.add(dTime);
    if (startSet) {
      if (Objects.isNull(trackDataXYR)) {
        initialGuess.update(startX, startY, startOrientation, pose);
        closedTrack = initialGuess.isClosed();
      }
      if (Objects.isNull(trackDataXYR) && closedTrack) {
        // current track is not available or no longer valid
        Tensor ctrpointsXY = initialGuess.getControlPointGuess(SPACING, CP_RESOLUTION);
        if (Objects.nonNull(ctrpointsXY)) {
          // we have a guess
          // TODO do this more elegantly
          // Tensor radiusCtrPoints = Tensors.vector(i -> Quantity.of(1, SI.METER), ctrpointsXY.get(0).length());
          constraints = new LinkedList<>();
          /* if (closedTrack) {
           * // no constraints at the moment
           * } else {
           * constraints.add(refinenement.new PositionalStartConstraint());
           * constraints.add(refinenement.new PositionalEndConstraint());
           * } */
          if (closedTrack) {
            trackDataXYR = refinenement.getRefinedTrack( //
                Tensor.of(ctrpointsXY.stream().map(xy -> xy.copy().append(Quantity.of(1, SI.METER)))), //
                RealScalar.of(8), 100, closedTrack, constraints);
          }
          /* else
           * trackData = refinenement.getRefinedTrack(//
           * ctrpoints.get(0), //
           * ctrpoints.get(1), //
           * radiusCtrPoints, RealScalar.of(8), 10, closedTrack, constraints); */
          if (Objects.nonNull(trackDataXYR)) {
            // valid refinement
            // create Track
            // To consider: high startup cost -> maybe don't do this in every step
            // TODO JPH/MH
            lastTrack = MPCBSplineTrack.withOffset(trackDataXYR, RADIUS_OFFSET, closedTrack);
            timeSinceLastTrackUpdate = Quantity.of(0, SI.SECOND);
            trackRender.setTrack(lastTrack.bSplineTrack());
          } else {
            System.out.println("no solution found!");
            lastTrack = null;
            trackRender.setTrack(null);
          }
        }
      } else //
      if (closedTrack) {
        System.out.println(++count);
        // refine
        System.out.println("refine");
        trackDataXYR = refinenement.getRefinedTrack(trackDataXYR, RealScalar.of(8), 10, closedTrack, constraints);
        // consider: slower track update
        if (Objects.nonNull(trackDataXYR)) {
          lastTrack = MPCBSplineTrack.withOffset(trackDataXYR, RADIUS_OFFSET, closedTrack);
          trackRender.setTrack(lastTrack.bSplineTrack());
        }
      }
      oldWasClosed = closedTrack;
    }
    return Optional.ofNullable(lastTrack);
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (Objects.nonNull(lastTrack))
      trackRender.render(geometricLayer, graphics);
    else
      initialGuess.render(geometricLayer, graphics);
  }

  public void renderHR(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (Objects.nonNull(lastTrack))
      trackRender.render(geometricLayer, graphics);
    else
      initialGuess.renderHR(geometricLayer, graphics);
  }
}
