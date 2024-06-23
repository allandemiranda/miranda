package lu.forex.system.operations;

import jakarta.persistence.LockModeType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import lu.forex.system.dtos.NewTickDto;
import lu.forex.system.dtos.TickDto;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@RequestMapping("/ticks")
public interface TickOperation {

  @GetMapping("/{symbolName}")
  @ResponseStatus(HttpStatus.OK)
  List<TickDto> getTicksBySymbolName(final @PathVariable @NotBlank @Size(max = 6, min = 6) String symbolName);

  @PostMapping("/{symbolName}")
  @ResponseStatus(HttpStatus.CREATED)
  @Lock(LockModeType.NONE)
  @Transactional
  String addTickBySymbolName(final @RequestBody @Valid NewTickDto newTickDto, final @PathVariable @NotBlank @Size(max = 6, min = 6) String symbolName);

  @GetMapping("/init/{symbolName}")
  @ResponseStatus(HttpStatus.OK)
  void initDataBase(final @PathVariable @NotBlank @Size(max = 6, min = 6) String symbolName, final @RequestBody @NotBlank String dateFileName);

}
