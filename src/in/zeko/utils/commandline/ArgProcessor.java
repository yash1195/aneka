package in.zeko.utils.commandline;

/**
 * Processes command line arguments.
 */
public class ArgProcessor {

  public static ProcessedArgs process(String[] args) {

    return ProcessedArgs
            .builder()
            .maxThreadCount(Integer.parseInt(args[0].split("=")[1]))
            .serverIp(args[1].split("=")[1])
            .applicationPort(Integer.parseInt(args[2].split("=")[1]))
            .dayNumber(Integer.parseInt(args[3].split("=")[1]))
            .userPopulation(Integer.parseInt(args[4].split("=")[1]))
            .testsPerPhase(Integer.parseInt(args[5].split("=")[1]))
            .build();
  }
}
