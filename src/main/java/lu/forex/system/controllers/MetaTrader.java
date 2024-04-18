package lu.forex.system.controllers;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller()
@RequestMapping(value = "/metatrader")
public class MetaTrader {

  @RequestMapping(value = "/info", method = RequestMethod.POST)
  public ResponseEntity<List<String>> postData(@RequestParam("dataTime") LocalDateTime dateTime,
      @RequestParam("pair") String pair, @RequestParam("ask") double ask,
      @RequestParam("bid") double bid, @RequestParam("timeFrame") String timeFrame, @RequestParam("digits") int digits) {

      final List<String> open = Arrays.asList("OPEN", dateTime.toString(), pair, String.valueOf(bid),
          String.valueOf(ask), timeFrame, String.valueOf(digits));
      System.out.println(Arrays.toString(open.toArray()));
      return ResponseEntity.ok(open);
  }
}
