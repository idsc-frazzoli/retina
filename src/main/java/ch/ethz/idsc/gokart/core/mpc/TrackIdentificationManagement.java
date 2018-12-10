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
  double startOrientation = 0;
  Scalar radiusOffset = Quantity.of(0.8, SI.METER);
  Scalar spacing = RealScalar.of(1.5);// TODO should be meters
  Scalar controlPointResolution = RealScalar.of(0.5);
  MPCBSplineTrack lastTrack;
  TrackRender trackRender;
  Boolean closedTrack = false;
  Stopwatch lastTrackReset = Stopwatch.started();
  List<TrackRefinenement.TrackConstraint> constraints = new LinkedList<>();
  long openTrackValid = 1000000000;
  TrackRefinenement.TrackConstraint startConstraint = null;
  TrackRefinenement.TrackConstraint endConstraint = null;
  boolean startSet = false;

  public TrackIdentificationManagement(PlanableOccupancyGrid planableOccupancyGrid) {
    this.occupancyGrid = planableOccupancyGrid;
    this.initialGuess = new TrackLayoutInitialGuess(occupancyGrid);
    this.refinenement = new TrackRefinenement(occupancyGrid);
    Tensor gridSize = occupancyGrid.getGridSize();
    width = gridSize.Get(0).number().intValue();
    heigth = gridSize.Get(1).number().intValue();
  }

  public boolean setStart(GokartPoseEvent gpe) {
    if (gpe != null)
      return setStart(gpe.getPose());
    else
      return false;
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
    } else
      return false;
  }

  public void update(GokartPoseEvent gpe) {
    update(gpe.getPose());
  }

  public void update(Tensor pose) {
    if (startSet) {
      if (trackData == null || //
          (!closedTrack && lastTrackReset.display_nanoSeconds() > openTrackValid)) {
        // current track is not available or no longer valid
        initialGuess.update(startX, startY, startOrientation, pose);
        closedTrack = initialGuess.isClosed();
        Tensor ctrpoints = initialGuess.getControlPointGuess(spacing, controlPointResolution);
        if (ctrpoints != null && closedTrack) {
          // we have a guess
          // TODO: do this more elegantly
          Tensor radiusCtrPoints = Tensors.empty();
          for (int i = 0; i < ctrpoints.get(0).length(); i++) {
            radiusCtrPoints.append(Quantity.of(1, SI.METER));
          }
          constraints = new LinkedList<>();
          if (closedTrack) {
            // no constraints at the moment
          } else {
            // TODO: introduce constraints
          }
          trackData = refinenement.getRefinedTrack(//
              ctrpoints.get(0), //
              ctrpoints.get(1), //
              radiusCtrPoints, RealScalar.of(8), 100, closedTrack, constraints);
          if (trackData != null) {
            // valid refinement
            // create Track
            // To consider: high startup cost -> maybe don't do this in every step
            lastTrack = new MPCBSplineTrack(trackData, radiusOffset);
            trackRender = null;
          }
        }
      } else {
        // refine
        System.out.println("refine");
        trackData = refinenement.getRefinedTrack(trackData, RealScalar.of(8), 1, closedTrack, constraints);
        // consider: slower track update
        if (trackData != null) {
          lastTrack = new MPCBSplineTrack(trackData, radiusOffset);
          trackRender = null;
        }
      }
    }
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
}
