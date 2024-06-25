package lu.forex.system.controllers;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lu.forex.system.batchs.InitDataJob;
import lu.forex.system.operations.BatchJobsOperation;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Getter(AccessLevel.PRIVATE)
@Log4j2
public class BatchJobsController implements BatchJobsOperation {

  private final InitDataJob initDataJob;

  @Override
  public void startBatchJob() {
    log.warn("Starting batch jobs");
    this.getInitDataJob().start();
  }
}
