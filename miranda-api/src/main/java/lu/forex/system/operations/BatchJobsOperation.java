package lu.forex.system.operations;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@RequestMapping("/batch")
public interface BatchJobsOperation {

  @GetMapping("/initData/start")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  void startBatchJob();
}
