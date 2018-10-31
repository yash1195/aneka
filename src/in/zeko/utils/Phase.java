package in.zeko.utils;

public class Phase {

  private int activeThreadCount;
  private String tag;
  private int testsPerPhase;
  private int startInterval;
  private int endInterval;
  private int userPopulation;
  private int maxStepCount;

  public int getActiveThreadCount() {
    return activeThreadCount;
  }

  public String getTag() {
    return tag;
  }

  public int getTestsPerPhase() {
    return testsPerPhase;
  }

  public int getStartInterval() {return startInterval;}

  public int getEndInterval() {return endInterval;}

  public int getUserPopulation() {return userPopulation;}

  public int getMaxStepCount() {return maxStepCount;}

  private Phase() {}

  public static Builder builder(){
    return new Builder();
  }

  public static class Builder {

    Phase phase;

    public Builder() {
      phase = new Phase();
    }

    public Builder tag(String tag) {
      phase.tag = tag;
      return this;
    }

    public Builder activeThreadCount(int activeThreadCount) {
      phase.activeThreadCount = activeThreadCount;
      return this;
    }

    public Builder testsPerPhase(int testsPerPhase) {
      phase.testsPerPhase = testsPerPhase;
      return this;
    }

    public Builder startInterval(int startInterval) {
      phase.startInterval = startInterval;
      return this;
    }

    public Builder endInterval(int endInterval) {
      phase.endInterval = endInterval;
      return this;
    }

    public Builder userPopulation(int userPopulation) {
      phase.userPopulation = userPopulation;
      return this;
    }

    public Builder maxStepCount(int stepCount) {
      phase.maxStepCount = stepCount;
      return this;
    }

    public Phase build() {
      return phase;
    }
  }
}
