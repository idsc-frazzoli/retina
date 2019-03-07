// code by jph
package ch.ethz.idsc.demo.vc;

import java.io.File;
import java.util.stream.Stream;

import ch.ethz.idsc.retina.util.img.ImageFilesAnimation;
import ch.ethz.idsc.tensor.io.HomeDirectory;

/* package */ enum ImagesToGif {
  ;
  public static void main(String[] args) throws Exception {
    File dir = ClusterAreaEvaluationListener.DIRECTORY_CLUSTERS;
    dir.mkdir();
    File output = HomeDirectory.Pictures("clusterDemo.gif");
    int period_ms = 100; // duration for each frame
    Stream<File> stream = Stream.of(dir.listFiles()).sorted().limit(2500);
    ImageFilesAnimation.gif(output, period_ms, stream);
  }
}
