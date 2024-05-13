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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CandlestickResponseDtoTest {

  private ValidatorFactory validatorFactory;

  @BeforeEach
  void setUp() {
    validatorFactory = Validation.buildDefaultValidatorFactory();
  }

  //CandlestickResponseDto candlestickResponseDto = new CandlestickResponseDto(UUID.randomUUID(), symbolResponseDto, TimeFrame.M15, LocalDateTime.now(), 1d, 1d, 1d, 1d);

  @Test
  void testCandlestickResponseIdNotNullIsValid() {
    //given
    final Validator validator = validatorFactory.getValidator();
    final CandlestickResponseDto candlestickResponseDto = new CandlestickResponseDto(UUID.randomUUID(), null, null, null, 0d, 0d, 0d, 0d);
    //when
    final Set<ConstraintViolation<CandlestickResponseDto>> validate = validator.validate(candlestickResponseDto);
    //then
    Assertions.assertFalse(validate.stream()
        .anyMatch(candlestickResponseDtoConstraintViolation -> "id".equals(candlestickResponseDtoConstraintViolation.getPropertyPath().toString())));
  }

  @Test
  void testCandlestickResponseIdNullIsInvalid() {
    //given
    final Validator validator = validatorFactory.getValidator();
    final CandlestickResponseDto candlestickResponseDto = new CandlestickResponseDto(null, null, null, null, 0d, 0d, 0d, 0d);
    //when
    final Set<ConstraintViolation<CandlestickResponseDto>> validate = validator.validate(candlestickResponseDto);
    //then
    Assertions.assertTrue(validate.stream().anyMatch(
        candlestickResponseDtoConstraintViolation -> "id".equals(candlestickResponseDtoConstraintViolation.getPropertyPath().toString())
                                                     && "{jakarta.validation.constraints.NotNull.message}".equals(
            candlestickResponseDtoConstraintViolation.getMessageTemplate())));
  }

  @Test
  void testCandlestickResponseSymbolNotNullIsValid() {
    //given
    final Validator validator = validatorFactory.getValidator();
    final SymbolResponseDto symbol = Mockito.mock(SymbolResponseDto.class);
    final CandlestickResponseDto candlestickResponseDto = new CandlestickResponseDto(null, symbol, null, null, 0d, 0d, 0d, 0d);
    //when
    final Set<ConstraintViolation<CandlestickResponseDto>> validate = validator.validate(candlestickResponseDto);
    //then
    Assertions.assertFalse(validate.stream().anyMatch(
        candlestickResponseDtoConstraintViolation -> "symbol".equals(candlestickResponseDtoConstraintViolation.getPropertyPath().toString())));
  }

  @Test
  void testCandlestickResponseSymbolNullIsInvalid() {
    //given
    final Validator validator = validatorFactory.getValidator();
    CandlestickResponseDto candlestickResponseDto = new CandlestickResponseDto(null, null, null, null, 0d, 0d, 0d, 0d);
    //when
    final Set<ConstraintViolation<CandlestickResponseDto>> validate = validator.validate(candlestickResponseDto);
    //then
    Assertions.assertTrue(validate.stream().anyMatch(
        candlestickResponseDtoConstraintViolation -> "symbol".equals(candlestickResponseDtoConstraintViolation.getPropertyPath().toString())
                                                     && "{jakarta.validation.constraints.NotNull.message}".equals(
            candlestickResponseDtoConstraintViolation.getMessageTemplate())));
  }

  @ParameterizedTest
  @EnumSource(TimeFrame.class)
  void testCandlestickResponseTimeFrameNotNullIsValid(TimeFrame timeFrame) {
    //given
    final Validator validator = validatorFactory.getValidator();
    final CandlestickResponseDto candlestickResponseDto = new CandlestickResponseDto(null, null, timeFrame, null, 0d, 0d, 0d, 0d);
    //when
    final Set<ConstraintViolation<CandlestickResponseDto>> validate = validator.validate(candlestickResponseDto);
    //then
    Assertions.assertFalse(validate.stream().anyMatch(
        candlestickResponseDtoConstraintViolation -> "timeFrame".equals(candlestickResponseDtoConstraintViolation.getPropertyPath().toString())));
  }

  @Test
  void testCandlestickResponseTimeFrameNullIsInvalid() {
    //given
    final Validator validator = validatorFactory.getValidator();
    CandlestickResponseDto candlestickResponseDto = new CandlestickResponseDto(null, null, null, null, 0d, 0d, 0d, 0d);
    //when
    final Set<ConstraintViolation<CandlestickResponseDto>> validate = validator.validate(candlestickResponseDto);
    //then
    Assertions.assertTrue(validate.stream().anyMatch(
        candlestickResponseDtoConstraintViolation -> "timeFrame".equals(candlestickResponseDtoConstraintViolation.getPropertyPath().toString())
                                                     && "{jakarta.validation.constraints.NotNull.message}".equals(
            candlestickResponseDtoConstraintViolation.getMessageTemplate())));
  }

  @Test
  void testCandlestickResponseTimestampNotNullIsValid() {
    //given
    final Validator validator = validatorFactory.getValidator();
    final CandlestickResponseDto candlestickResponseDtoPast = new CandlestickResponseDto(null, null, null, LocalDateTime.now().minusYears(1), 0d, 0d,
        0d, 0d);
    final CandlestickResponseDto candlestickResponseDtoNow = new CandlestickResponseDto(null, null, null, LocalDateTime.now(), 0d, 0d, 0d, 0d);
    final CandlestickResponseDto candlestickResponseDtoFuture = new CandlestickResponseDto(null, null, null, LocalDateTime.now().plusYears(1), 0d, 0d,
        0d, 0d);
    //when
    final Set<ConstraintViolation<CandlestickResponseDto>> validatePast = validator.validate(candlestickResponseDtoPast);
    final Set<ConstraintViolation<CandlestickResponseDto>> validateNow = validator.validate(candlestickResponseDtoNow);
    final Set<ConstraintViolation<CandlestickResponseDto>> validateFuture = validator.validate(candlestickResponseDtoFuture);
    //then
    Assertions.assertFalse(validatePast.stream().anyMatch(
        candlestickResponseDtoConstraintViolation -> "timestamp".equals(candlestickResponseDtoConstraintViolation.getPropertyPath().toString())
                                                     && "{jakarta.validation.constraints.NotNull.message}".equals(
            candlestickResponseDtoConstraintViolation.getMessageTemplate())));
    Assertions.assertFalse(validateNow.stream().anyMatch(
        candlestickResponseDtoConstraintViolation -> "timestamp".equals(candlestickResponseDtoConstraintViolation.getPropertyPath().toString())
                                                     && "{jakarta.validation.constraints.NotNull.message}".equals(
            candlestickResponseDtoConstraintViolation.getMessageTemplate())));
    Assertions.assertFalse(validateFuture.stream().anyMatch(
        candlestickResponseDtoConstraintViolation -> "timestamp".equals(candlestickResponseDtoConstraintViolation.getPropertyPath().toString())
                                                     && "{jakarta.validation.constraints.NotNull.message}".equals(
            candlestickResponseDtoConstraintViolation.getMessageTemplate())));
  }

  @Test
  void testCandlestickResponseTimestampNullIsInvalid() {
    //given
    final Validator validator = validatorFactory.getValidator();
    CandlestickResponseDto candlestickResponseDto = new CandlestickResponseDto(null, null, null, null, 0d, 0d, 0d, 0d);
    //when
    final Set<ConstraintViolation<CandlestickResponseDto>> validate = validator.validate(candlestickResponseDto);
    //then
    Assertions.assertTrue(validate.stream().anyMatch(
        candlestickResponseDtoConstraintViolation -> "timestamp".equals(candlestickResponseDtoConstraintViolation.getPropertyPath().toString())
                                                     && "{jakarta.validation.constraints.NotNull.message}".equals(
            candlestickResponseDtoConstraintViolation.getMessageTemplate())));
  }

  @Test
  void testCandlestickResponseTimestampFutureIsInvalid() {
    //given
    final Validator validator = validatorFactory.getValidator();
    final CandlestickResponseDto candlestickResponseDtoPast = new CandlestickResponseDto(null, null, null, LocalDateTime.now().minusYears(1), 0d, 0d,
        0d, 0d);
    final CandlestickResponseDto candlestickResponseDtoNow = new CandlestickResponseDto(null, null, null, LocalDateTime.now(), 0d, 0d, 0d, 0d);
    final CandlestickResponseDto candlestickResponseDtoFuture = new CandlestickResponseDto(null, null, null, LocalDateTime.now().plusYears(1), 0d, 0d,
        0d, 0d);
    //when
    final Set<ConstraintViolation<CandlestickResponseDto>> validatePast = validator.validate(candlestickResponseDtoPast);
    final Set<ConstraintViolation<CandlestickResponseDto>> validateNow = validator.validate(candlestickResponseDtoNow);
    final Set<ConstraintViolation<CandlestickResponseDto>> validateFuture = validator.validate(candlestickResponseDtoFuture);
    System.out.println(validateFuture);
    //then
    Assertions.assertFalse(validatePast.stream().anyMatch(
        candlestickResponseDtoConstraintViolation -> "timestamp".equals(candlestickResponseDtoConstraintViolation.getPropertyPath().toString())));
    Assertions.assertFalse(validateNow.stream().anyMatch(
        candlestickResponseDtoConstraintViolation -> "timestamp".equals(candlestickResponseDtoConstraintViolation.getPropertyPath().toString())));
    Assertions.assertTrue(validateFuture.stream().anyMatch(
        candlestickResponseDtoConstraintViolation -> "timestamp".equals(candlestickResponseDtoConstraintViolation.getPropertyPath().toString())
                                                     && "{jakarta.validation.constraints.Past.message}".equals(
            candlestickResponseDtoConstraintViolation.getMessageTemplate())));
  }

  @ParameterizedTest
  @ValueSource(doubles = {Double.MIN_EXPONENT, -1d, 0d})
  void testCandlestickResponseHighNotZeroOrNegativeIsInvalid(double price) {
    //given
    final Validator validator = validatorFactory.getValidator();
    final CandlestickResponseDto candlestickResponseDto = new CandlestickResponseDto(null, null, null, null, price, 0d, 0d, 0d);
    //when
    final Set<ConstraintViolation<CandlestickResponseDto>> validate = validator.validate(candlestickResponseDto);
    //then
    Assertions.assertTrue(validate.stream().anyMatch(
        candlestickResponseDtoConstraintViolation -> "high".equals(candlestickResponseDtoConstraintViolation.getPropertyPath().toString())
                                                     && "{jakarta.validation.constraints.Positive.message}".equals(
            candlestickResponseDtoConstraintViolation.getMessageTemplate())));
  }

  @Test
  void testCandlestickResponseHighPlusOrEqualThanLowAndCloseAndOpenIsValid() {
    //given
    final Validator validator = validatorFactory.getValidator();
    final CandlestickResponseDto candlestickResponseDtoPlus = new CandlestickResponseDto(null, null, null, null, 2d, 1d, 1d, 1d);
    final CandlestickResponseDto candlestickResponseDtoEqual = new CandlestickResponseDto(null, null, null, null, 1d, 1d, 1d, 1d);
    //when
    final Set<ConstraintViolation<CandlestickResponseDto>> validatePlus = validator.validate(candlestickResponseDtoPlus);
    final Set<ConstraintViolation<CandlestickResponseDto>> validateEqual = validator.validate(candlestickResponseDtoEqual);
    //then
    Assertions.assertFalse(validatePlus.stream().anyMatch(
        candlestickResponseDtoConstraintViolation -> "{lu.forex.system.annotations.CandlestickRepresentation}".equals(
            candlestickResponseDtoConstraintViolation.getMessageTemplate())));
    Assertions.assertFalse(validateEqual.stream().anyMatch(
        candlestickResponseDtoConstraintViolation -> "{lu.forex.system.annotations.CandlestickRepresentation}".equals(
            candlestickResponseDtoConstraintViolation.getMessageTemplate())));
  }

  @ParameterizedTest
  @ValueSource(doubles = {Double.MIN_EXPONENT, -1d, 0d})
  void testCandlestickResponseLowNotZeroOrNegativeIsInvalid(double price) {
    //given
    final Validator validator = validatorFactory.getValidator();
    final CandlestickResponseDto candlestickResponseDto = new CandlestickResponseDto(null, null, null, null, 0d, price, 0d, 0d);
    //when
    final Set<ConstraintViolation<CandlestickResponseDto>> validate = validator.validate(candlestickResponseDto);
    //then
    Assertions.assertTrue(validate.stream().anyMatch(
        candlestickResponseDtoConstraintViolation -> "low".equals(candlestickResponseDtoConstraintViolation.getPropertyPath().toString())
                                                     && "{jakarta.validation.constraints.Positive.message}".equals(
            candlestickResponseDtoConstraintViolation.getMessageTemplate())));
  }

  @Test
  void testCandlestickResponseLowLowerOrEqualThanHighAndCloseAndOpenIsValid() {
    //given
    final Validator validator = validatorFactory.getValidator();
    final CandlestickResponseDto candlestickResponseDtoLower = new CandlestickResponseDto(null, null, null, null, 2d, 1d, 2d, 2d);
    final CandlestickResponseDto candlestickResponseDtoEqual = new CandlestickResponseDto(null, null, null, null, 1d, 1d, 1d, 1d);
    //when
    final Set<ConstraintViolation<CandlestickResponseDto>> validateLower = validator.validate(candlestickResponseDtoLower);
    final Set<ConstraintViolation<CandlestickResponseDto>> validateEqual = validator.validate(candlestickResponseDtoEqual);
    //then
    Assertions.assertFalse(validateLower.stream().anyMatch(
        candlestickResponseDtoConstraintViolation -> "{lu.forex.system.annotations.CandlestickRepresentation}".equals(
            candlestickResponseDtoConstraintViolation.getMessageTemplate())));
    Assertions.assertFalse(validateEqual.stream().anyMatch(
        candlestickResponseDtoConstraintViolation -> "{lu.forex.system.annotations.CandlestickRepresentation}".equals(
            candlestickResponseDtoConstraintViolation.getMessageTemplate())));
  }

  @ParameterizedTest
  @ValueSource(doubles = {Double.MIN_EXPONENT, -1d, 0d})
  void testCandlestickResponseOpenNotZeroOrNegativeIsInvalid(double price) {
    //given
    final Validator validator = validatorFactory.getValidator();
    final CandlestickResponseDto candlestickResponseDto = new CandlestickResponseDto(null, null, null, null, 0d, 0d, price, 0d);
    //when
    final Set<ConstraintViolation<CandlestickResponseDto>> validate = validator.validate(candlestickResponseDto);
    //then
    Assertions.assertTrue(validate.stream().anyMatch(
        candlestickResponseDtoConstraintViolation -> "open".equals(candlestickResponseDtoConstraintViolation.getPropertyPath().toString())
                                                     && "{jakarta.validation.constraints.Positive.message}".equals(
            candlestickResponseDtoConstraintViolation.getMessageTemplate())));
  }

  @Test
  void testCandlestickResponseOpenLowerThanLowAndPlusThanHighIsInvalid() {
    //given
    final Validator validator = validatorFactory.getValidator();
    final CandlestickResponseDto candlestickResponseDtoLower = new CandlestickResponseDto(null, null, null, null, 2d, 2d, 1d, 2d);
    final CandlestickResponseDto candlestickResponseDtoPlus = new CandlestickResponseDto(null, null, null, null, 1d, 1d, 2d, 1d);
    //when
    final Set<ConstraintViolation<CandlestickResponseDto>> validateLower = validator.validate(candlestickResponseDtoLower);
    final Set<ConstraintViolation<CandlestickResponseDto>> validatePlus = validator.validate(candlestickResponseDtoPlus);
    //then
    Assertions.assertTrue(validateLower.stream().anyMatch(
        candlestickResponseDtoConstraintViolation -> "{lu.forex.system.annotations.CandlestickRepresentation}".equals(
            candlestickResponseDtoConstraintViolation.getMessageTemplate())));
    Assertions.assertTrue(validatePlus.stream().anyMatch(
        candlestickResponseDtoConstraintViolation -> "{lu.forex.system.annotations.CandlestickRepresentation}".equals(
            candlestickResponseDtoConstraintViolation.getMessageTemplate())));
  }

  @ParameterizedTest
  @ValueSource(doubles = {Double.MIN_EXPONENT, -1d, 0d})
  void testCandlestickResponseCloseNotZeroOrNegativeIsInvalid(double price) {
    //given
    final Validator validator = validatorFactory.getValidator();
    final CandlestickResponseDto candlestickResponseDto = new CandlestickResponseDto(null, null, null, null, 0d, 0d, 0d, price);
    //when
    final Set<ConstraintViolation<CandlestickResponseDto>> validate = validator.validate(candlestickResponseDto);
    //then
    Assertions.assertTrue(validate.stream().anyMatch(
        candlestickResponseDtoConstraintViolation -> "close".equals(candlestickResponseDtoConstraintViolation.getPropertyPath().toString())
                                                     && "{jakarta.validation.constraints.Positive.message}".equals(
            candlestickResponseDtoConstraintViolation.getMessageTemplate())));
  }

  @Test
  void testCandlestickResponseCloseLowerThanLowAndPlusThanHighIsInvalid() {
    //given
    final Validator validator = validatorFactory.getValidator();
    final CandlestickResponseDto candlestickResponseDtoLower = new CandlestickResponseDto(null, null, null, null, 2d, 2d, 2d, 1d);
    final CandlestickResponseDto candlestickResponseDtoPlus = new CandlestickResponseDto(null, null, null, null, 1d, 1d, 1d, 2d);
    //when
    final Set<ConstraintViolation<CandlestickResponseDto>> validateLower = validator.validate(candlestickResponseDtoLower);
    final Set<ConstraintViolation<CandlestickResponseDto>> validatePlus = validator.validate(candlestickResponseDtoPlus);
    //then
    Assertions.assertTrue(validateLower.stream().anyMatch(
        candlestickResponseDtoConstraintViolation -> "{lu.forex.system.annotations.CandlestickRepresentation}".equals(
            candlestickResponseDtoConstraintViolation.getMessageTemplate())));
    Assertions.assertTrue(validatePlus.stream().anyMatch(
        candlestickResponseDtoConstraintViolation -> "{lu.forex.system.annotations.CandlestickRepresentation}".equals(
            candlestickResponseDtoConstraintViolation.getMessageTemplate())));
  }
}