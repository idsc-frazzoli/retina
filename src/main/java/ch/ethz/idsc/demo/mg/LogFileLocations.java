// code by mg
package ch.ethz.idsc.demo.mg;

import java.io.File;

import ch.ethz.idsc.subare.util.UserHome;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.ResourceData;

public enum LogFileLocations {
  /** extracted part of DUBI4 log file */
  DUBI4a("20180307T154859_0cd18c6bExtracted.lcm"), //
  /** 22sec, only noise */
  DUBI7("20180308T162751_45b9eed8.lcm.00"), //
  /** manual driving with tons of cones */
  DUBI8("20180412T114245_7e5b46c2.lcm.00"), //
  /** extracted slow round of DUBI8 */
  DUBI8a("20180412T114245_7e5b46c2Extracted1.lcm"), //
  /** extracted double round of DUBI8 */
  DUBI8b("20180412T114245_7e5b46c2Extracted2.lcm"), //
  /** extracted andy's double round of DUBI8 */
  DUBI8c("20180412T114245_7e5b46c2Extracted3.lcm"), //
  /** extracted andy's fast double round of DUBI8 */
  DUBI8d("20180412T114245_7e5b46c2Extracted4.lcm"), //
  /** extracted ale's fast double round of DUBI8 */
  DUBI8e("20180412T114245_7e5b46c2Extracted5.lcm"), //
  /** manual driving with cones, zoomed out camera --> helmet blocking corner of image
   * extracted slow round of DUBI9 */
  DUBI9a("20180412T143634_7e5b46c2Extracted1.lcm"), //
  /** extracted another slow round of DUBI9 */
  DUBI9b("20180412T143634_7e5b46c2Extracted2.lcm"), //
  /** extracted very slow round of DUBI9 */
  DUBI9c("20180412T143634_7e5b46c2Extracted3.lcm"), //
  /** extracted slow round of DUBI9, tight track */
  DUBI9d("20180412T143634_7e5b46c2Extracted4.lcm"), //
  /** extracted fast round of DUBI9, tight track */
  DUBI9e("20180412T143634_7e5b46c2Extracted5.lcm"), //
  /** manual driving, second half with cones and stripes on floor. no helmet blocking sight */
  DUBI10("20180412T152900_7e5b46c2.lcm.00"), //
  /** extracted fast round of DUBI10 */
  DUBI10a("20180412T152900_7e5b46c2Extracted1.lcm"), //
  /** extracted another fast round of DUBI10 */
  DUBI10b("20180412T152900_7e5b46c2Extracted2.lcm"), //
  /** extracted round of DUBI10 with cones and stripes */
  DUBI10c("20180412T152900_7e5b46c2Extracted3.lcm"), //
  /** extracted round of DUBI10 with cones and stripes */
  DUBI10d("20180412T152900_7e5b46c2Extracted4.lcm"), //
  /** extracted fast round of DUBI10 with cones and stripes */
  DUBI10e("20180412T152900_7e5b46c2Extracted5.lcm"), //
  /** manual driving with only stripes on floor */
  DUBI11("20180412T164740_7e5b46c2.lcm.00"), //
  /** extracted round of DUBI11, Valentina driving */
  DUBI11a("20180412T164740_7e5b46c2Extracted1.lcm"), //
  /** extracted round of DUBI11, Mario driving, helmet slightly blocking sight */
  DUBI11b("20180412T164740_7e5b46c2Extracted2.lcm"), //
  /** extracted slow round of DUBI11, Mario driving */
  DUBI11c("20180412T164740_7e5b46c2Extracted3.lcm"), //
  /** extracted fast round of DUBI11, Jan driving */
  DUBI11d("20180412T164740_7e5b46c2Extracted4.lcm"), //
  /** new camera pose, calibration images under "Dubi first try". camera calibration available */
  DUBI12("20180430T164704_0e959fc6.lcm.00"), //
  /** extracted slow round of DUBI12, camera zoomed and looking down. */
  DUBI12a("20180430T164704_0e959fc6Extracted1.lcm"), //
  /** extracted slightly faster round of DUBI12, camera zoomed and looking down. */
  DUBI12b("20180430T164704_0e959fc6Extracted2.lcm"), //
  /** extracted faster & chaotic round of DUBI12, camera zoomed and looking down. */
  DUBI12c("20180430T164704_0e959fc6Extracted3.lcm"), //
  /** extracted slow round of DUBI12, camera zoomed and looking down. */
  DUBI12d("20180430T164704_0e959fc6Extracted4.lcm"), //
  /** extracted fast & chaotic round of DUBI12, camera zoomed and looking down. */
  DUBI12e("20180430T164704_0e959fc6Extracted5.lcm"), //
  /** log with new optimized camera pose */
  DUBI13("20180507T143002_588d4c6c.lcm.00"), //
  /** driving in 8 shape, cones and duct tape markings */
  DUBI13a("20180507T143002_588d4c6Extracted1.lcm"), //
  /** driving in 8 shape, cones and duct tape markings */
  DUBI13b("20180507T143002_588d4c6Extracted2.lcm"), //
  /** slow driving in 8 shape, cones and duct tape markings */
  DUBI13c("20180507T143002_588d4c6Extracted3.lcm"), //
  /** only ducttape markings, calibration available */
  DUBI14("20180514T153139_767e5417.lcm.00"), //
  // ** extracted single round */
  DUBI14a("20180514T153139_767e5417Extracted1.lcm"),
  // ** extracted double round */
  DUBI14b("20180514T153139_767e5417Extracted2.lcm"),
  /** ducttape markings and cones on outer side, calibration available */
  DUBI15("20180514T155248_767e5417.lcm.00"),
  /** extracted double round */
  DUBI15a("20180514T155248_767e5417Extracted1.lcm"),
  /** extracted double round */
  DUBI15b("20180514T155248_767e5417Extracted2.lcm"),
  /** slow double round, additional cones */
  DUBI15c("20180514T155248_767e5417Extracted3.lcm"),
  /** extracted double round, additional cones */
  DUBI15d("20180514T155248_767e5417Extracted4.lcm"),
  /** short file, duckie thrown in front of gokart */
  DUBI15e("20180514T155248_767e5417Extracted5.lcm"),
  /** much more way points on the floor, driving in eight shape */
  DUBI16("20180813T165630_2c569ed8.lcm.00"),
  /** one slow round */
  DUBI16a("20180813T165630_2c569ed8Extracted1.lcm"),
  /** one round */
  DUBI16b("20180813T165630_2c569ed8Extracted2.lcm"),
  /** one faster round, different starting point */
  DUBI16c("20180813T165630_2c569ed8Extracted3.lcm"),
  /** one round with aprupt acceleration and braking */
  DUBI16d("20180813T165630_2c569ed8Extracted4.lcm"),
  /** six rounds with varying speed */
  DUBI16e("20180813T165630_2c569ed8Extracted5.lcm"),
  /** autonomously driving around circle shape with lots of way points */
  DUBI17("20180913T164921_34b3470d.lcm.00"),
  /** 2.5 laps autonomously driving around */
  DUBI17a("20180913T164921_34b3470dExtracted1.lcm"),
  /** 2 laps autonomously driving around */
  DUBI17b("20180913T164921_34b3470dExtracted2.lcm"),
  /** 2 laps autonomously driving around */
  DUBI17c("20180913T164921_34b3470dExtracted3.lcm"),
  /** 1.5 laps autonomously Jan driving around */
  DUBI17d("20180913T164921_34b3470dExtracted4.lcm"),
  /** autonomously Jan driving around */
  DUBI17e("20180913T164921_34b3470dExtracted5.lcm"),
  /** autonomously driving along straight line in center of hangar */
  DUBI18("20180913T174600_34b3470d.lcm.00"),
  /** autonomously driving along straight line very slowly */
  DUBI18a("20180913T174600_34b3470dExtracted1.lcm"),
  /** autonomously driving along straight line slow speed */
  DUBI18b("20180913T174600_34b3470dExtracted2.lcm"),
  /** autonomously driving along straight line slow speed */
  DUBI18c("20180913T174600_34b3470dExtracted3.lcm"),
  /** autonomously driving along straight line slow speed */
  DUBI18d("20180913T174600_34b3470dExtracted4.lcm"),
  /** autonomously driving along straight line a bit faster */
  DUBI18e("20180913T174600_34b3470dExtracted5.lcm"),
  /** autonomously driving along straight line a bit faster */
  DUBI18f("20180913T174600_34b3470dExtracted6.lcm");
  // ---
  private final String filename;

  private LogFileLocations(String filename) {
    this.filename = filename;
  }

  public File getFile() {
    final File root;
    switch (UserHome.file("").getName()) {
    case "datahaki":
      root = new File("/media/datahaki/media/ethz/gokart/topic/davis_extracted_logs");
      break;
    default:
      root = UserHome.file("logs");
      break;
    }
    File file = new File(root, filename);
    if (!file.isFile())
      System.err.println("file does not exist:\n" + file);
    return file;
  }

  /** relative to src/main/resources/ */
  String calibrationFileName() {
    return "/demo/mg/" + name().substring(0, name().length() - 1) + ".csv";
  }

  public Tensor calibration() {
    return ResourceData.of(calibrationFileName());
  }
}
