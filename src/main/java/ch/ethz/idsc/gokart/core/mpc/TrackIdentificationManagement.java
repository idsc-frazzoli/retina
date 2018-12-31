package ch.ethz.idsc.gokart.core.mpc;

import java.awt.Graphics2D;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.gui.top.TrackRender;
import ch.ethz.idsc.owl.data.Stopwatch;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.LinearSolve;
import ch.ethz.idsc.tensor.qty.Quantity;

public class TrackIdentificationManagement implements RenderInterface {
  public final PlanableOccupancyGrid occupancyGrid;
  public final TrackLayoutInitialGuess initialGuess;
  public final TrackRefinenement refinenement;
  Tensor trackData = null;
  int startX = -1;
  int startY = -1;
  int width = 0;
  int heigth = 0;
  int count = 0;
  double startOrientation = 0;
  Scalar radiusOffset = Quantity.of(0.4, SI.METER);
  Scalar spacing = RealScalar.of(1.5);// TODO should be meters
  Scalar controlPointResolution = RealScalar.of(0.5);
  MPCBSplineTrack lastTrack;
  TrackRender trackRender;
  Boolean closedTrack = false;
  Boolean oldWasClosed = false;
  Stopwatch lastTrackReset = Stopwatch.started();
  List<TrackRefinenement.TrackConstraint> constraints = new LinkedList<>();
  Scalar openTrackValid = Quantity.of(1, SI.SECOND);
  Scalar timeSinceLastTrackUpdate = Quantity.of(0, SI.SECOND);
  List<TrackRefinenement.TrackConstraint> trackConstraints = null;
  boolean startSet = false;

  public TrackIdentificationManagement(PlanableOccupancyGrid planableOccupancyGrid) {
    this.occupancyGrid = planableOccupancyGrid;
    this.initialGuess = new TrackLayoutInitialGuess(occupancyGrid);
    this.refinenement = new TrackRefinenement(occupancyGrid);
    Tensor gridSize = occupancyGrid.getGridSize();
    width = gridSize.Get(0).number().intValue();
    heigth = gridSize.Get(1).number().intValue();
  }

  public boolean setStart(GokartPoseEvent gokartPoseEvent) {
    return gokartPoseEvent != null && setStart(gokartPoseEvent.getPose());
  }

  public void resetStart() {
    startSet = false;
  }

  public void resetTrack() {
    lastTrack = null;
    trackRender = null;
  }

  public boolean isStartSet() {
    return startSet;
  }

  /** set start position
   * @param pose [x[m], y[m], angle]
   * @return true if valid start position */
  public boolean setStart(Tensor pose) {
    Tensor transform = occupancyGrid.getTransform();
    Tensor hpos = Tensors.of(pose.Get(0), pose.Get(1), Quantity.of(1, SI.METER));
    Tensor pixelPos = LinearSolve.of(transform, hpos);
    startX = pixelPos.Get(0).number().intValue();
    startY = pixelPos.Get(1).number().intValue();
    startOrientation = pose.Get(2).number().doubleValue();
    if (startX >= 0 && startX < width && startY >= 0 && startY < heigth) {
      startSet = true;
      return true;
    }
    return false;
  }

  public MPCBSplineTrack update(GokartPoseEvent gpe, Scalar dTime) {
    return update(gpe.getPose(), dTime);
  }

  public MPCBSplineTrack update(Tensor pose, Scalar dTime) {
    System.out.println("update called: " + timeSinceLastTrackUpdate);
    timeSinceLastTrackUpdate = timeSinceLastTrackUpdate.add(dTime);
    if (startSet) {
      if (trackData == null) {
        initialGuess.update(startX, startY, startOrientation, pose);
        closedTrack = initialGuess.isClosed();
      }
      if (trackData == null && closedTrack) {
        // current track is not available or no longer valid
        Tensor ctrpoints = initialGuess.getControlPointGuess(spacing, controlPointResolution);
        if (ctrpoints != null) {
          // we have a guess
          // TODO: do this more elegantly
          Tensor radiusCtrPoints = Tensors.vector(i -> Quantity.of(1, SI.METER), ctrpoints.get(0).length());
          constraints = new LinkedList<>();
          /* if (closedTrack) {
           * // no constraints at the moment
           * } else {
           * constraints.add(refinenement.new PositionalStartConstraint());
           * constraints.add(refinenement.new PositionalEndConstraint());
           * } */
          if (closedTrack)
            trackData = refinenement.getRefinedTrack(//
                ctrpoints.get(0), //
                ctrpoints.get(1), //
                radiusCtrPoints, RealScalar.of(8), 100, closedTrack, constraints);
          /* else
           * trackData = refinenement.getRefinedTrack(//
           * ctrpoints.get(0), //
           * ctrpoints.get(1), //
           * radiusCtrPoints, RealScalar.of(8), 10, closedTrack, constraints); */
          if (trackData != null) {
            // valid refinement
            // create Track
            // To consider: high startup cost -> maybe don't do this in every step
            lastTrack = new MPCBSplineTrack(trackData, radiusOffset, closedTrack);
            timeSinceLastTrackUpdate = Quantity.of(0, SI.SECOND);
            trackRender = null;
          } else {
            System.out.println("no solution found!");
            lastTrack = null;
          }
        }
      } else if (closedTrack) {
        System.out.println(count++);
        // refine
        System.out.println("refine");
        trackData = refinenement.getRefinedTrack(trackData, RealScalar.of(8), 10, closedTrack, constraints);
        // consider: slower track update
        if (trackData != null) {
          lastTrack = new MPCBSplineTrack(trackData, radiusOffset, closedTrack);
          trackRender = null;
        }
      }
      oldWasClosed = closedTrack;
    }
    return lastTrack;
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (lastTrack != null) {
      if (trackRender == null)
        trackRender = new TrackRender(lastTrack);
      trackRender.render(geometricLayer, graphics);
    } else {
      initialGuess.render(geometricLayer, graphics);
    }
  }
  
  public void renderHR(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (lastTrack != null) {
      if (trackRender == null)
        trackRender = new TrackRender(lastTrack);
      trackRender.renderHR(geometricLayer, graphics);
    } else {
      initialGuess.renderHR(geometricLayer, graphics);
    }
  }
}
