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
  double startOrientation = 0;
  Scalar spacing = RealScalar.of(1.5);// TODO should be meters
  Scalar controlPointResolution = RealScalar.of(0.5);
  MPCBSplineTrack lastTrack;
  Boolean closedTrack = false;
  Stopwatch lastTrackReset = Stopwatch.started();
  long openTrackValid = 1000000000;
  TrackRefinenement.TrackConstraint startConstraint = null;
  TrackRefinenement.TrackConstraint endConstraint = null;

  public TrackIdentificationManagement(PlanableOccupancyGrid planableOccupancyGrid) {
    this.occupancyGrid = planableOccupancyGrid;
    this.initialGuess = new TrackLayoutInitialGuess(occupancyGrid);
    this.refinenement = new TrackRefinenement(occupancyGrid);
  }

  public boolean setStart(GokartPoseEvent gpe) {
    return setStart(gpe.getPose());
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
    return true;
  }

  public void update(GokartPoseEvent gpe) {
    update(gpe.getPose());
  }

  public void update(Tensor pose) {
    if (trackData == null || //
        !closedTrack && lastTrackReset.display_nanoSeconds() > openTrackValid) {
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
        List<TrackRefinenement.TrackConstraint> constraints = new LinkedList<>();
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
          
        }
      }
    }
    /* initialGuess.update(startX, startY, startOrientation, gpe.getPose(), track);
     * initialGuess.render(gl, graphics);
     * if (initialGuess.isClosed()) {
     * Scalar spacing = RealScalar.of(1.5);
     * Scalar controlPointResolution = RealScalar.of(0.5);
     * Tensor ctrpoints = initialGuess.getControlPointGuess(spacing, controlPointResolution);
     * if (ctrpoints != null) {
     * Tensor radiusCtrPoints = Tensors.empty();
     * for (int i = 0; i < ctrpoints.get(0).length(); i++) {
     * radiusCtrPoints.append(Quantity.of(1, SI.METER));
     * }
     * trackData = trackRefinenement.getRefinedTrack(//
     * ctrpoints.get(0), //
     * ctrpoints.get(1), //
     * radiusCtrPoints, RealScalar.of(8), 100, initialGuess.isClosed(),null);
     * } else {
     * System.out.println("no sensible track found!");
     * }
     * }
     * } else {
     * System.out.println("refining old track!");
     * trackData = trackRefinenement.getRefinedTrack(//
     * trackData, RealScalar.of(8), 1, true, null);
     * }
     * }if(trackData!=null)
     * 
     * {
     * Tensor radCtrP = Tensors.vector((i) -> trackData.get(2).Get(i).add(Quantity.of(0.7, SI.METER)), trackData.get(2).length());
     * track = new BSplineTrack(trackData.get(0), trackData.get(1), radCtrP);
     * }if(track!=null)
     * {
     * TrackRender trackRender = new TrackRender(track);
     * trackRender.render(gl, graphics); */
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    // TODO Auto-generated method stub
  }
}
