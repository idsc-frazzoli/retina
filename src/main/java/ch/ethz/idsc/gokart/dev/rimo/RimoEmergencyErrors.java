// code by jph
package ch.ethz.idsc.gokart.dev.rimo;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/* package */ enum RimoEmergencyErrors {
  INSTANCE;
  // ---
  private final Map<Short, RimoEmergencyError> map = new HashMap<>();

  private RimoEmergencyErrors() {
    for (RimoEmergencyError ree : RimoEmergencyError.values())
      map.put(ree.code, ree);
  }

  public Optional<RimoEmergencyError> ofCode(short code) {
    return Optional.ofNullable(map.get(code));
  }

  /* package */ int size() {
    return map.size();
  }
}
