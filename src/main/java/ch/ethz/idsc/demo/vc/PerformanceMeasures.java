package ch.ethz.idsc.demo.vc;

public class PerformanceMeasures {
  public final double precision;
  public final double recall;

  public PerformanceMeasures(double recall, double precision) {
    this.precision = precision;
    this.recall = recall;
  }
}
