// code by mh
package ch.ethz.idsc.gokart.core.sound;

/* package */ class SimpleExciter implements SoundExciter {
  private static final float TWO_PI = (float) (2 * Math.PI);
  // ---
  private final float absFrequency;
  private final float relFrequency;
  private final float powerFactor;
  private float sinePosition = 0;
  private float dsinePosition;

  public SimpleExciter(float absFrequency, float relFrequency, float powerFactor) {
    this.absFrequency = absFrequency;
    this.relFrequency = relFrequency;
    this.powerFactor = powerFactor;
  }

  @Override
  public float getNextValue(GokartSoundState state, float dt) {
    dsinePosition = dt * (state.speed * relFrequency + absFrequency);
    sinePosition += dsinePosition;
    sinePosition %= TWO_PI;
    float sineVal = (float) Math.sin(sinePosition);
    float powered = (float) (Math.signum(sineVal) * Math.pow(Math.abs(sineVal), powerFactor * state.power + 1));
    return powered;
  }
}
