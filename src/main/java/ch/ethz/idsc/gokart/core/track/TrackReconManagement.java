// code by jph
package ch.ethz.idsc.gokart.core.track;

import java.io.File;
import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.gokart.core.map.OccupancyGrid;
import ch.ethz.idsc.gokart.core.mpc.MPCBSplineTrack;
import ch.ethz.idsc.owl.math.region.BoundedBoxRegion;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.time.SystemTimestamp;
import ch.ethz.idsc.sophus.math.Extract2D;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.mat.LinearSolve;
import ch.ethz.idsc.tensor.qty.Quantity;

public class TrackReconManagement {
  private static final Scalar RADIUS_SEED = Quantity.of(1.0, SI.METER);
  private static final Scalar RADIUS_OFFSET = Quantity.of(1, SI.METER);//was 0.5
  private static final Scalar SPACING = RealScalar.of(1.2); // TODO MH/JPH should be meters
  private static final Scalar CP_RESOLUTION = RealScalar.of(0.5);//scales number of control points 
  private static final int RESOLUTION = 8;
  // ---
  private final OccupancyGrid occupancyGrid;
  private final TrackLayoutInitialGuess trackLayoutInitialGuess;
  private final TrackRefinement trackRefinement;
  // ---
  private Tensor trackDataXYR = null;
  // private int startX = -1;
  // private int startY = -1;
  private Tensor start = null;
  // private int width = 0;
  // private int height = 0;
  private Region<Tensor> region;
  private int count = 0;
  private double startOrientation = 0;
  private boolean closedTrack = false;
  private boolean newSolutionNeeded = false;

  public TrackReconManagement(OccupancyGrid occupancyGrid) {
    this.occupancyGrid = occupancyGrid;
    this.trackLayoutInitialGuess = new TrackLayoutInitialGuess(occupancyGrid);
    this.trackRefinement = new TrackRefinement(occupancyGrid);
    // Tensor gridSize = occupancyGrid.getGridSize();
    // width = gridSize.Get(0).number().intValue();
    // height = gridSize.Get(1).number().intValue();
    Tensor halfSize = occupancyGrid.getGridSize().multiply(RationalScalar.HALF);
    region = BoundedBoxRegion.fromCenterAndRadius(halfSize, halfSize);
  }

  /** clears track data: center line, boundaries, ...
   * but keeps start position */
  public void computeTrack() {
    trackDataXYR = null;
  }

  public void exportTrack() {
    if (Objects.nonNull(trackDataXYR))
      try {
        File folder = HomeDirectory.Documents("TrackID");
        folder.mkdir();
        Export.of(new File(folder, "track_" + SystemTimestamp.asString() + ".csv"), trackDataXYR.map(Magnitude.METER));
      } catch (Exception exception) {
        exception.printStackTrace();
      }
  }

  public boolean isStartSet() {
    // TODO JPH design bad
    // return startX >= 0 //
    // && startX < width //
    // && startY >= 0 //
    // && startY < height;
    return Objects.nonNull(start) && region.isMember(Extract2D.FUNCTION.apply(start).map(Magnitude.METER));
  }

  /** set start position
   * 
   * @param pose {x[m], y[m], angle} */
  public void setStart(Tensor pose) {
    Tensor transform = occupancyGrid.getTransform();
    Tensor hpos = Tensors.of(pose.Get(0), pose.Get(1), Quantity.of(1, SI.METER));
    // Tensor pixelPos = LinearSolve.of(transform, hpos);
    // System.out.println("pixelPos=" + pixelPos);
    start = LinearSolve.of(transform, hpos);
    System.out.println("pixelPos=" + start);
    // startX = pixelPos.Get(0).number().intValue();
    // startY = pixelPos.Get(1).number().intValue();
    startOrientation = pose.Get(2).number().doubleValue();
    // occupancyGrid.clearStart(startX, startY, startOrientation);
    occupancyGrid.clearStart(start.Get(0).number().intValue(), start.Get(1).number().intValue(), startOrientation);
  }

  public Optional<MPCBSplineTrack> update(Tensor pose) {
    // System.out.println("update called: " + timeSinceLastTrackUpdate);
    MPCBSplineTrack lastTrack = null;
    if (!closedTrack || newSolutionNeeded) {
      // trackLayoutInitialGuess.update(startX, startY, startOrientation, pose);
      trackLayoutInitialGuess.update(start.Get(0).number().intValue(), start.Get(1).number().intValue(), startOrientation, pose);
      if (trackLayoutInitialGuess.getRouteLength() > 0) {
        closedTrack = trackLayoutInitialGuess.isClosed();
        if (closedTrack) {
          // current track is not available or no longer valid
          Optional<Tensor> optional = trackLayoutInitialGuess.getControlPointGuess(SPACING, CP_RESOLUTION);
          if (optional.isPresent()) {
            Tensor ctrpointsXY = optional.get();
            // we have a guess
            Tensor newTrackDataXYR = trackRefinement.getRefinedTrack( //
                Tensor.of(ctrpointsXY.stream().map(xy -> xy.copy().append(RADIUS_SEED))), //
                RESOLUTION, 10, closedTrack);
            if (Objects.nonNull(newTrackDataXYR)) {
              trackDataXYR = newTrackDataXYR;
              newSolutionNeeded = false;
            } else
              newSolutionNeeded = true;
          }
        } else {
          // we have a partial track
          // check if route is long enough
          if (2 < trackLayoutInitialGuess.getRouteLength()) {
            Optional<Tensor> optional = trackLayoutInitialGuess.getControlPointGuess(SPACING, CP_RESOLUTION);
            if (optional.isPresent()) {
              Tensor ctrpointsXY = optional.get();
              Tensor newTrackDataXYR = Tensor.of(ctrpointsXY.stream().map(xy -> xy.copy().append(RADIUS_SEED)));
              // System.out.println("open track");
              newTrackDataXYR = trackRefinement.getRefinedTrack(newTrackDataXYR, RESOLUTION, 10, closedTrack);
              if (Objects.nonNull(newTrackDataXYR))
                trackDataXYR = newTrackDataXYR;
            }
          }
        }
      }
    } else { // closedTrack == true
      System.out.println(++count);
      // refine
      System.out.println("refine");
      Tensor newTrackDataXYR = Objects.nonNull(trackDataXYR) //
          ? trackRefinement.getRefinedTrack(trackDataXYR, RESOLUTION, 3, closedTrack) //
          : null;
      if (Objects.nonNull(newTrackDataXYR))
        trackDataXYR = newTrackDataXYR;
      else
        newSolutionNeeded = true;
      // consider: slower track update
    }
    if (Objects.nonNull(trackDataXYR))
      lastTrack = MPCBSplineTrack.withOffset(trackDataXYR, RADIUS_OFFSET, closedTrack);
    return Optional.ofNullable(lastTrack);
  }

  public Tensor getTrackData() {
    return trackDataXYR;
  }

  public TrackLayoutInitialGuess getTrackLayoutInitialGuess() {
    return trackLayoutInitialGuess;
  }

  public boolean isClosedTrack() {
    return closedTrack;
  }
}
