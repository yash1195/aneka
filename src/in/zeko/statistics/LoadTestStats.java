package in.zeko.statistics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import in.zeko.enums.ResponseStatus;

public class LoadTestStats {

  private static LoadTestStats instance = null;

  // stats
  private List<Long> allRequestLatencies;
  public List<RequestStat> allRequestStats;
  private long totalRequestCount;
  private long totalSuccessfulRequestCount;
  private long startTime;
  private long endTime;
  private long totalLatency;

  // results
  List<JobResult> jobResults;

  private LoadTestStats() {
    jobResults = new ArrayList<JobResult>();
    allRequestStats = new ArrayList<RequestStat>();
  }

  public synchronized void addJobResult(JobResult jobResult) {
    jobResults.add(jobResult);
  }

  public static LoadTestStats getInstance() {
    if (instance == null) {
      instance = new LoadTestStats();
    }
    return instance;
  }

  public void processData(){
    allRequestLatencies = new ArrayList<Long>();

    for (JobResult jobResult : jobResults) {
      for (RequestStat requestStat: jobResult.requestStats) {

        allRequestStats.add(requestStat);
        if (requestStat.responseStatus == ResponseStatus.SUCCESSFUL) {
          totalSuccessfulRequestCount++;
          allRequestLatencies.add(requestStat.latency);
          totalLatency += requestStat.latency;
        }
        totalRequestCount++;
      }
    }

    Collections.sort(allRequestLatencies);
  }

  public void markTestStart() {
    this.startTime = System.currentTimeMillis();
  }

  private long getMedian() {
    long median;
    if (allRequestLatencies.size() % 2 == 0)
      median = (allRequestLatencies.get(allRequestLatencies.size()/2) +
              allRequestLatencies.get(allRequestLatencies.size()/2 - 1))/2;
    else
      median = allRequestLatencies.get(allRequestLatencies.size()/2);

    return median;
  }

  private long getMean() {

    return (totalLatency / totalRequestCount);
  }

  private long calculatePercentile(double percentile) {
    int percentileIndex = (int) Math.ceil((percentile / 100) * allRequestLatencies.size());
    return allRequestLatencies.get(percentileIndex - 1);
  }

  public void markTestEnd() {
    this.endTime = System.currentTimeMillis();
  }

  public void displayStats() {

    System.out.println("Statistics: ");
    System.out.println();
    System.out.println("Total number of requests: " + totalRequestCount);
    System.out.println("Total number of successful responses: " + totalSuccessfulRequestCount);
    System.out.println("Test Wall time : " + (endTime - startTime)/1000 + " seconds");
    System.out.println("Throughput: " + ((totalRequestCount * 1000) / (endTime - startTime)) + " requests/second");
    System.out.println("Mean latency: " + getMean() + " milliseconds");
    System.out.println("Median latency: " + getMedian() + " milliseconds");
    System.out.println("99th Percentile: " + calculatePercentile(99) + " milliseconds");
    System.out.println("95th Percentile: " + calculatePercentile(95) + " milliseconds");
  }
}
