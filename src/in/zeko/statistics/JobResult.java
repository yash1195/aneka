package in.zeko.statistics;

import java.util.List;

import in.zeko.utils.Phase;

public class JobResult {

  public Phase phase;
  public List<RequestStat> requestStats;
  public long jobLatency;
  public int threadId;

  public JobResult(Phase phase, List<RequestStat> requestStats, long jobLatency, int threadId) {
    this.phase = phase;
    this.requestStats = requestStats;
    this.jobLatency = jobLatency;
    this.threadId = threadId;
  }
}
