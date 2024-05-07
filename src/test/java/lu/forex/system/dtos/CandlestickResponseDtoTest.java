package lu.forex.system.dtos;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import lu.forex.system.enums.TimeFrame;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CandlestickResponseDtoTest {

  private ValidatorFactory validatorFactory;

  @Mock
  private SymbolResponseDto symbolResponseDto;

  @BeforeEach
  void setUp() {
    validatorFactory = Validation.buildDefaultValidatorFactory();
  }

  @Test
  void testTest() {

    final Validator validator = validatorFactory.getValidator();

    CandlestickResponseDto candlestickResponseDto = new CandlestickResponseDto(UUID.randomUUID(), symbolResponseDto, TimeFrame.M15,
        LocalDateTime.now(), 1d, 2d, 1d, 1d);

    final Set<ConstraintViolation<CandlestickResponseDto>> validate = validator.validate(candlestickResponseDto);
    System.out.println(validate);
    Assertions.assertTrue(validate.isEmpty());
  }
}