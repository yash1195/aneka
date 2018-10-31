package in.zeko.utils.commandline;

/**
 * Wrapper for processed arguments.
 */
public class ProcessedArgs {

  private int maxThreadCount;
  private int testsPerPhase;
  private String serverIp;
  private int applicationPort;
  private String endpoint;
  private int dayNumber;
  private int userPopulation;

  private ProcessedArgs() {}

  public int getMaxThreadCount() {
    return maxThreadCount;
  }

  public int getTestsPerPhase() {
    return testsPerPhase;
  }

  public String getServerIp() {
    return serverIp;
  }

  public int getApplicationPort() {
    return applicationPort;
  }

  public String getEndpoint() {
    return endpoint;
  }

  public int getDayNumber() {return dayNumber;}

  public int getUserPopulation() {return userPopulation;}

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {

    private ProcessedArgs processedArgs;

    public Builder() {
      processedArgs = new ProcessedArgs();
    }

    public Builder maxThreadCount(int maxThreadCount) {
      processedArgs.maxThreadCount = maxThreadCount;
      return this;
    }

    public Builder testsPerPhase(int testsPerPhase) {
      processedArgs.testsPerPhase = testsPerPhase;
      return this;
    }

    public Builder serverIp(String serverIp) {
      processedArgs.serverIp = serverIp;
      return this;
    }

    public Builder applicationPort(int applicationPort) {
      processedArgs.applicationPort = applicationPort;
      return this;
    }

    public Builder endpoint(String endpoint) {
      processedArgs.endpoint = endpoint;
      return this;
    }

    public Builder dayNumber(int dayNumber) {
      processedArgs.dayNumber = dayNumber;
      return this;
    }

    public Builder userPopulation(int userPopulation) {
      processedArgs.userPopulation = userPopulation;
      return this;
    }

    public ProcessedArgs build() {
      return processedArgs;
    }

  }
}
