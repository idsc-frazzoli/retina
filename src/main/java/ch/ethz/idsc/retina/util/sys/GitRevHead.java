// code by niam jen wei and jph
package ch.ethz.idsc.retina.util.sys;

import java.util.Arrays;
import java.util.List;

/** Class to return the current git details for tracking purposes. Minor problem:
 * if new log file is created while git is just pulled, will give wrong details */
public class GitRevHead {
  public static String getHash() {
    List<String> list = Arrays.asList(//
        "git", //
        "rev-parse", //
        "HEAD" //
    );
    return SystemShellCommand.exec(list);
  }

  public static String getTag() {
    List<String> list = Arrays.asList(//
        "git", //
        "describe", //
        "--tags" //
    );
    return SystemShellCommand.exec(list);
  }

  /** Retrieve the current software version (according to git head tag).
   * 
   * E.g. v1.0.2 would return a byte array: [1, 0, 2].
   * 
   * If parsing failed, returns [-1, -1, -1].
   * 
   * @return The current software version. */
  public static byte[] getSoftwareVersion() {
    String tag = getTag();
    /* Tag can be something like this: 'v1.2.2-65-gae87606'
     * 
     * Remove prefix "v" */
    String tagBare = tag.replace("v", "");
    /* Split at dots '.' and at dashes '-'
     * 
     * Result: ['1' '2' '2' '65' 'gae87606'] */
    String[] split = tagBare.split("[.-]+");
    if (split.length < 3)
      return new byte[] { -1, -1, -1 };
    /* Take and parse the first three entries. */
    byte[] output = new byte[3];
    for (int i = 0; i < output.length; i++) {
      try {
        output[i] = Byte.parseByte(split[i]);
      } catch (NumberFormatException e) {
        return new byte[] { -1, -1, -1 };
      }
    }
    return output;
  }
}
