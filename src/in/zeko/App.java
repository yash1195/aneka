package in.zeko;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;

import in.zeko.jobs.WebServiceJobMeta;
import in.zeko.statistics.LoadTestStats;
import in.zeko.statistics.StatDaemon;
import in.zeko.statistics.StatWriter;
import in.zeko.utils.Endpoints;
import in.zeko.utils.Phase;
import in.zeko.utils.StepCountWebServiceLoadTest;
import in.zeko.utils.commandline.ArgProcessor;
import in.zeko.utils.commandline.ProcessedArgs;

public class App {

  public static int MAX_STEP_COUNT = 1000;

  public static void main(String[] args) throws Exception {

    ProcessedArgs processedArgs = ArgProcessor.process(args);

    Endpoints appEndpoints = new Endpoints();
    appEndpoints.addEndpoint("postStepCount", "/StepServer5/webapi/stepCount");
    appEndpoints.addEndpoint("getCurrentStepCount", "/StepServer5/webapi/stepCount/current");
    appEndpoints.addEndpoint("getSingleDayStepCount", "/StepServer5/webapi/stepCount/single");
    appEndpoints.addEndpoint("getRangeStepCount", "/StepServer5/webapi/stepCount/range");


    WebServiceJobMeta metaData = new WebServiceJobMeta(
            processedArgs.getServerIp(),
            processedArgs.getApplicationPort(),
            appEndpoints
    );

    List<Phase> phases = new ArrayList<Phase>();

    // test 1

    // warmup
    int activeThreadCountForWarmup = processedArgs.getMaxThreadCount() / 10;
    phases.add(Phase.builder()
            .activeThreadCount(activeThreadCountForWarmup)
            .testsPerPhase(processedArgs.getTestsPerPhase())
            .startInterval(1)
            .endInterval(3)
            .tag("Warmup")
            .maxStepCount(MAX_STEP_COUNT)
            .userPopulation(processedArgs.getUserPopulation())
            .testsPerPhase(processedArgs.getTestsPerPhase())
            .build());

//    // loading
    int activeThreadCountForLoading = processedArgs.getMaxThreadCount() / 2;
    phases.add(Phase.builder()
            .activeThreadCount(activeThreadCountForLoading)
            .testsPerPhase(processedArgs.getTestsPerPhase())
            .startInterval(4)
            .endInterval(8)
            .tag("Loading")
            .maxStepCount(MAX_STEP_COUNT)
            .userPopulation(processedArgs.getUserPopulation())
            .testsPerPhase(processedArgs.getTestsPerPhase())
            .build());

    // Peak
    int activeThreadCountForPeak = processedArgs.getMaxThreadCount();
    phases.add(Phase.builder()
            .activeThreadCount(activeThreadCountForPeak)
            .testsPerPhase(processedArgs.getTestsPerPhase())
            .startInterval(9)
            .endInterval(19)
            .tag("Peak")
            .maxStepCount(MAX_STEP_COUNT)
            .userPopulation(processedArgs.getUserPopulation())
             .testsPerPhase(processedArgs.getTestsPerPhase())
            .build());

    // Cooldown
    int activeThreadCountForCooldown = processedArgs.getMaxThreadCount() / 4;
    phases.add(Phase.builder()
            .activeThreadCount(activeThreadCountForCooldown)
            .testsPerPhase(processedArgs.getTestsPerPhase())
            .startInterval(20)
            .endInterval(24)
            .tag("Cooldown")
            .maxStepCount(MAX_STEP_COUNT)
            .userPopulation(processedArgs.getUserPopulation())
            .testsPerPhase(processedArgs.getTestsPerPhase())
            .build());

    StepCountWebServiceLoadTest loadTest = new StepCountWebServiceLoadTest(phases, processedArgs.getMaxThreadCount(), metaData);

    CyclicBarrier statWriterBarrier = new CyclicBarrier(2);
    Thread statWriterThread = new Thread(new StatWriter("/Users/zeko/acad/cs6650-BSDS/project2/client/testResult-bonus.csv",
            loadTest, StatDaemon.getInstance(), statWriterBarrier));

    statWriterThread.start();
    loadTest.run();

    statWriterBarrier.await();

  }
}
