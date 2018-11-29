package ch.ethz.idsc.gokart.core.mpc;

import java.awt.Graphics2D;

import ch.ethz.idsc.gokart.core.map.BayesianOccupancyGrid;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;

public class TrackLayoutInitialGuess implements RenderInterface {
  private final BayesianOccupancyGrid occupancyGrid;

  public TrackLayoutInitialGuess(BayesianOccupancyGrid occupancyGrid) {
    this.occupancyGrid = occupancyGrid;
    
  }
  
  public void update() {
    
    
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    // TODO Auto-generated method stub
  }
}
