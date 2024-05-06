package lu.forex.system.entities.templates;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public interface TickTestTemplate {

  @Test
  void tickIsValid();

  @Test
  void tickIsValidTimestampOld();

  @Test
  void tickIsValidTimestampFuture();

  @Test
  void tickWithoutSymbolIsInvalid();

  @Test
  void tickWithoutTimestampIsInvalid();

  @ParameterizedTest
  @ValueSource(doubles = {0d, -1d, -2d})
  void tickWithNegativeOrZeroBidIsInvalid(double value);

  @ParameterizedTest
  @ValueSource(doubles = {0d, -1d, -2d})
  void tickWithNegativeAskIsInvalid(double value);

  @Test
  void testEqualsAndHashCode();

  @Test
  void testIdNotEqualsAndHashCode();

  @Test
  void testSymbolNotEqualsAndHashCode();

  @Test
  void testTimestampNotEqualsAndHashCode();
}
