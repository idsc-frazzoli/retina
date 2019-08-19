// code by jph
package ch.ethz.idsc.demo.jph.video;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.demo.VideoBackground;
import ch.ethz.idsc.tensor.io.HomeDirectory;

public enum RunVideoBackground {
  ;
  public static void main(String[] args) throws IOException {
    VideoBackground.render( //
        new File("/media/datahaki/data/gokart/lane/20190805/20190805T153837_02/log.lcm"), //
        VideoBackground._20190401, //
        HomeDirectory.Pictures("20190805T153837_02.png"));
  }
}
