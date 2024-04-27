package lu.forex.system.repositories;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import java.util.UUID;
import lu.forex.system.entities.Symbol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface SymbolRepository extends JpaRepository<Symbol, UUID>, JpaSpecificationExecutor<Symbol> {

  @NotNull
  Optional<Symbol> findByName(@NotNull @NotBlank String name);

  @Transactional
  @Modifying
  @Query("update Symbol s set s.digits = ?1, s.swapLong = ?2, s.swapShort = ?3 where s.name = ?4")
  void updateDigitsAndSwapLongAndSwapShortByNameContains(int digits, double swapLong, double swapShort, @NotNull @NotBlank String name);

  void deleteByName(@NotNull @NotBlank String name);
}