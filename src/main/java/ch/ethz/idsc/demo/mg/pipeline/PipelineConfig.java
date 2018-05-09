package ch.ethz.idsc.demo.mg.pipeline;

import java.io.IOException;

import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.retina.util.data.TensorProperties;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;

public class PipelineConfig {
  // general parameters
  public final Scalar width = RealScalar.of(240);
  public final Scalar height = RealScalar.of(180);
  public final Scalar unitConversion = RealScalar.of(1000);
  // event filtering
  public final Scalar filterConstant = RealScalar.of(500); // [us]
  public final Scalar boarder = RealScalar.of(4);
  // feature tracking
  public final Scalar initNumberOfBlobs = RealScalar.of(24);
  public final Scalar numberRows = RealScalar.of(6);
  public final Scalar initVariance = RealScalar.of(250);
  public final Scalar aUp = RealScalar.of(0.15);
  public final Scalar aDown = RealScalar.of(0.12);
  public final Scalar scoreThreshold = RealScalar.of(4e-4f);
  public final Scalar alphaOne = RealScalar.of(0.9);
  public final Scalar alphaTwo = RealScalar.of(0.998);
  public final Scalar alphaAttr = RealScalar.of(0.002);
  public final Scalar dAttr = RealScalar.of(50);
  public final Scalar dMerge = RealScalar.of(20);
  public final Scalar boundaryDistance = RealScalar.of(1);
  public final Scalar tau = RealScalar.of(8000);
  // feature selection
  public final Scalar upperBoarder = RealScalar.of(100);
  // image to world transform

  PipelineConfig() {
  }

  public static void main(String[] args) throws IOException {
    PipelineConfig test = new PipelineConfig();
    TensorProperties.manifest(UserHome.file("config.properties"), test);
  }
}
