package lu.forex.system.controllers;

import lu.forex.system.dtos.TickDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller()
@RequestMapping(value = "/metatrader")
public class MetaTrader {

  @PostMapping(value = "/info")
  public ResponseEntity<String> postData(@RequestBody TickDto tick) {

    return ResponseEntity.ok(tick.toString());
  }

  @GetMapping(value = "/novo")
  public ResponseEntity<String> getData() {
    System.out.println("OK");
    return ResponseEntity.ok("OK");
  }
}
