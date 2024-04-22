package lu.forex.system.services;

import lombok.AccessLevel;
import lombok.Getter;
import lu.forex.system.repositories.TickRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Getter(AccessLevel.PRIVATE)
public class TickService {

  private final TickRepository tickRepository;

  @Autowired
  public TickService(final TickRepository tickRepository) {
    this.tickRepository = tickRepository;
  }
}
