// code by jph
package ch.ethz.idsc.retina.demo.jph;

import java.io.File;

import ch.ethz.idsc.retina.app.AccumulateToGif;
import ch.ethz.idsc.retina.io.dat.DatFileSupplier;
import ch.ethz.idsc.retina.io.txt.TxtFileSupplier;
import ch.ethz.idsc.retina.supply.DvsEventSupplier;
import ch.ethz.idsc.retina.supply.ImagesDvsEventSupplier;
import ch.ethz.idsc.retina.supply.ProceduralDvsEventSupplier;
import ch.ethz.idsc.retina.supply.synth.Waves;
import ch.ethz.idsc.retina.util.io.ImageDimensions;
import ch.ethz.idsc.retina.util.io.UserHome;

class AccumulateToGifTest {
  static void _dat(String name) throws Exception {
    File file = new File("/media/datahaki/media/ethz/dvs/wp.doc.ic.ac.uk_pb2114_datasets", //
        name + ".dat");
    final int WINDOW_US = 50000;
    AccumulateToGif.of( //
        new DatFileSupplier(file, ImageDimensions.IMPERIAL_COLLEGE), //
        UserHome.Pictures(name + ".gif"), WINDOW_US);
  }

  static void _txt(String name) throws Exception {
    File file = new File("/media/datahaki/media/ethz/davis", name);
    final int WINDOW_US = 50000;
    AccumulateToGif.of( //
        new TxtFileSupplier(new File(file, "events.txt"), ImageDimensions.UZ), //
        UserHome.Pictures(name + ".gif"), WINDOW_US);
  }

  static void _procedural(DvsEventSupplier dvsEventSupplier, String name) throws Exception {
    final int WINDOW_US = 50_000;
    AccumulateToGif.of(dvsEventSupplier, UserHome.Pictures(name + ".gif"), WINDOW_US, 20_000);
  }

  static void demo() throws Exception {
    File dir = new File("/media/datahaki/media/ethz/slowmo/out");
    ImagesDvsEventSupplier imagesDvsEventSupplier = //
        new ImagesDvsEventSupplier(dir, ImageDimensions.UZ);
    // imagesDvsEventSupplier.limit = 100;
    _procedural(imagesDvsEventSupplier, "cat");
  }

  public static void main(String[] args) throws Exception {
    // _dat("jumping");
    // _txt("shapes_6dof");
    // _txt("shapes_translation");
    // _txt("davis240c_line_scene");
    _procedural(new ProceduralDvsEventSupplier(ImageDimensions.IMPERIAL_COLLEGE, 10_000_000), "synth2");
    _procedural(Waves.create(ImageDimensions.IMPERIAL_COLLEGE), "synth1");
    // /media/datahaki/media/ethz/slowmo/out
    // File dir = new File("/media/datahaki/media/ethz/davis/shapes_translation/images");
  }
}
