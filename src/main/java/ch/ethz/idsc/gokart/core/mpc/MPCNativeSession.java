// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/** the session counts messages according to their IDs */
/* package */ class MPCNativeSession {
  private final Map<Integer, Integer> messageCounter = new HashMap<>();

  /** gets a unique ID for any object that inherits MPCNative */
  int getMessageId(MPCNativeMessage mpcNativeMessage) {
    int prefix = mpcNativeMessage.getMessageType().ordinal();
    Integer current = messageCounter.get(prefix);
    if (Objects.isNull(current))
      current = 0;
    messageCounter.put(prefix, current + 1);
    return current;
  }
}
