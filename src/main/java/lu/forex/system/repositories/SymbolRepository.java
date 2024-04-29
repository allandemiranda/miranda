package lu.forex.system.repositories;

import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import lu.forex.system.entities.Symbol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface SymbolRepository extends JpaRepository<Symbol, String>, JpaSpecificationExecutor<Symbol> {

  @NonNull
  Optional<@NotNull Symbol> findFirstByNameOrderByNameAsc(@NonNull String name);

  @Transactional
  void deleteByName(@NonNull String name);

  @Transactional
  @Modifying
  @Query("update Symbol s set s.digits = ?1, s.swapLong = ?2, s.swapShort = ?3 where s.name = ?4")
  void updateDigitsAndSwapLongAndSwapShortByName(@NonNull int digits, @NonNull double swapLong, @NonNull double swapShort, @NonNull String name);

}