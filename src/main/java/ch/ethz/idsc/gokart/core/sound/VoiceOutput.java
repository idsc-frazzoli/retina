// code by jph
package ch.ethz.idsc.gokart.core.sound;

/* package */ enum VoiceOutput {
  AISignal, //
  CalibrationSignal, //
  HumanSignal, //
  ObstacleDetectedWarning, //
  TrackIDSignal, //
  TVSignal, //
  ;
  public String resource() {
    return "/gokart/voice/" + name() + ".wav";
  }
}
