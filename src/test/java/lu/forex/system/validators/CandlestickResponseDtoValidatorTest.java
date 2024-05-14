package lu.forex.system.validators;

import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext;
import lu.forex.system.annotations.CandlestickRepresentation;
import lu.forex.system.dtos.CandlestickResponseDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CandlestickResponseDtoValidatorTest {

  @Mock
  private ConstraintValidatorContext context;

  @InjectMocks
  private CandlestickResponseDtoValidator validator;

  @Mock
  private CandlestickRepresentation constraintAnnotation;

  @Test
  void testCandlestickEntityValidator() {
    //given
    final var candlestick = Mockito.mock(CandlestickResponseDto.class);
    //when
    Mockito.when(candlestick.high()).thenReturn(5d);
    Mockito.when(candlestick.low()).thenReturn(2d);
    Mockito.when(candlestick.open()).thenReturn(3d);
    Mockito.when(candlestick.close()).thenReturn(4d);
    //then
    Assertions.assertTrue(validator.isValid(candlestick, context));
  }

  @Test
  void testCandlestickEntityValidatorPricesEqual() {
    //given
    final var candlestick = Mockito.mock(CandlestickResponseDto.class);
    //when
    Mockito.when(candlestick.high()).thenReturn(1d);
    Mockito.when(candlestick.low()).thenReturn(1d);
    Mockito.when(candlestick.open()).thenReturn(1d);
    Mockito.when(candlestick.close()).thenReturn(1d);
    //then
    Assertions.assertTrue(validator.isValid(candlestick, context));
  }

  @Test
  void testCandlestickEntityValidatorWhenHighLowerThanLowIsInvalid() {
    //given
    final var candlestick = Mockito.mock(CandlestickResponseDto.class);
    final ConstraintViolationBuilder constraintViolationBuilder = Mockito.mock(ConstraintViolationBuilder.class);
    final NodeBuilderCustomizableContext nodeBuilderCustomizableContext = Mockito.mock(NodeBuilderCustomizableContext.class);
    final String message = "anyString";

    //when
    Mockito.when(candlestick.high()).thenReturn(2d);
    Mockito.when(candlestick.low()).thenReturn(3d);

    Mockito.when(constraintAnnotation.message()).thenReturn(message);
    Mockito.when(context.buildConstraintViolationWithTemplate(Mockito.anyString())).thenReturn(constraintViolationBuilder);
    Mockito.when(constraintViolationBuilder.addPropertyNode(Mockito.anyString())).thenReturn(nodeBuilderCustomizableContext);
    //then
    Assertions.assertFalse(validator.isValid(candlestick, context));
    Mockito.verify(constraintViolationBuilder).addPropertyNode("high");
  }

  @Test
  void testCandlestickEntityValidatorWhenHighLowerThanOpenIsInvalid() {
    //given
    final var candlestick = Mockito.mock(CandlestickResponseDto.class);
    final ConstraintViolationBuilder constraintViolationBuilder = Mockito.mock(ConstraintViolationBuilder.class);
    final NodeBuilderCustomizableContext nodeBuilderCustomizableContext = Mockito.mock(NodeBuilderCustomizableContext.class);
    final String message = "anyString";
    //when
    Mockito.when(candlestick.high()).thenReturn(2d);
    Mockito.when(candlestick.low()).thenReturn(2d);
    Mockito.when(candlestick.open()).thenReturn(3d);

    Mockito.when(constraintAnnotation.message()).thenReturn(message);
    Mockito.when(context.buildConstraintViolationWithTemplate(Mockito.anyString())).thenReturn(constraintViolationBuilder);
    Mockito.when(constraintViolationBuilder.addPropertyNode(Mockito.anyString())).thenReturn(nodeBuilderCustomizableContext);
    //then
    Assertions.assertFalse(validator.isValid(candlestick, context));
    Mockito.verify(constraintViolationBuilder).addPropertyNode("high");
  }

  @Test
  void testCandlestickEntityValidatorWhenHighLowerThanCloseIsInvalid() {
    //given
    final var candlestick = Mockito.mock(CandlestickResponseDto.class);
    final ConstraintViolationBuilder constraintViolationBuilder = Mockito.mock(ConstraintViolationBuilder.class);
    final NodeBuilderCustomizableContext nodeBuilderCustomizableContext = Mockito.mock(NodeBuilderCustomizableContext.class);
    final String message = "anyString";
    //when
    Mockito.when(candlestick.high()).thenReturn(2d);
    Mockito.when(candlestick.low()).thenReturn(2d);
    Mockito.when(candlestick.open()).thenReturn(2d);
    Mockito.when(candlestick.close()).thenReturn(3d);

    Mockito.when(constraintAnnotation.message()).thenReturn(message);
    Mockito.when(context.buildConstraintViolationWithTemplate(Mockito.anyString())).thenReturn(constraintViolationBuilder);
    Mockito.when(constraintViolationBuilder.addPropertyNode(Mockito.anyString())).thenReturn(nodeBuilderCustomizableContext);
    //then
    Assertions.assertFalse(validator.isValid(candlestick, context));
    Mockito.verify(constraintViolationBuilder).addPropertyNode("high");
  }

  @Test
  void testCandlestickEntityValidatorWhenLowHigherThanOpenIsInvalid() {
    //given
    final var candlestick = Mockito.mock(CandlestickResponseDto.class);
    final ConstraintViolationBuilder constraintViolationBuilder = Mockito.mock(ConstraintViolationBuilder.class);
    final NodeBuilderCustomizableContext nodeBuilderCustomizableContext = Mockito.mock(NodeBuilderCustomizableContext.class);
    final String message = "anyString";
    //when
    Mockito.when(candlestick.high()).thenReturn(3d);
    Mockito.when(candlestick.low()).thenReturn(3d);
    Mockito.when(candlestick.open()).thenReturn(2d);
    Mockito.when(candlestick.close()).thenReturn(3d);

    Mockito.when(constraintAnnotation.message()).thenReturn(message);
    Mockito.when(context.buildConstraintViolationWithTemplate(Mockito.anyString())).thenReturn(constraintViolationBuilder);
    Mockito.when(constraintViolationBuilder.addPropertyNode(Mockito.anyString())).thenReturn(nodeBuilderCustomizableContext);
    //then
    Assertions.assertFalse(validator.isValid(candlestick, context));
    Mockito.verify(constraintViolationBuilder).addPropertyNode("low");
  }

  @Test
  void testCandlestickEntityValidatorWhenLowHigherThanCloseIsInvalid() {
    //given
    final var candlestick = Mockito.mock(CandlestickResponseDto.class);
    final ConstraintViolationBuilder constraintViolationBuilder = Mockito.mock(ConstraintViolationBuilder.class);
    final NodeBuilderCustomizableContext nodeBuilderCustomizableContext = Mockito.mock(NodeBuilderCustomizableContext.class);
    final String message = "anyString";
    //when
    Mockito.when(candlestick.high()).thenReturn(3d);
    Mockito.when(candlestick.low()).thenReturn(3d);
    Mockito.when(candlestick.open()).thenReturn(3d);
    Mockito.when(candlestick.close()).thenReturn(2d);

    Mockito.when(constraintAnnotation.message()).thenReturn(message);
    Mockito.when(context.buildConstraintViolationWithTemplate(Mockito.anyString())).thenReturn(constraintViolationBuilder);
    Mockito.when(constraintViolationBuilder.addPropertyNode(Mockito.anyString())).thenReturn(nodeBuilderCustomizableContext);
    //then
    Assertions.assertFalse(validator.isValid(candlestick, context));
    Mockito.verify(constraintViolationBuilder).addPropertyNode("low");
  }

}