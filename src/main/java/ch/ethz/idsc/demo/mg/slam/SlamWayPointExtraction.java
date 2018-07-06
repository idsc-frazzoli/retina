// code by mg
package ch.ethz.idsc.demo.mg.slam;

import ch.ethz.idsc.tensor.Tensor;
// extracts way points from a map
// as an input, we receive a MapProvider object and estimated go kart pose
// we compute which part of the map is currently seen by the go kart
// inside this part of the map, we extract the way points. First idea: threshold
// the way points can be connected. We can check is minimum curvature etc is fullfilled
public class SlamWayPointExtraction {
  private MapProvider map;
  private Tensor gokartPose;

  public void mapUpdate(MapProvider map, Tensor gokartPose) {
    // ..
  }

  private void computeWayPoints() {
    // think about how to compare new way point estimates with old ones
    // maybe introduce quality measure of way point?
    // maybe introduce way point object?
  }

  
  public double[][] getWayPoints() {
    // return way points for visualizatoin
    return null;
  }
}
