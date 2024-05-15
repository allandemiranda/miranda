package lu.forex.system.validators;

import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext;
import lu.forex.system.annotations.TickRepresentation;
import lu.forex.system.dtos.TickResponseDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TickResponseDtoValidatorTest {

  @Mock
  private ConstraintValidatorContext context;

  @InjectMocks
  private TickResponseDtoValidator validator;

  @Mock
  private TickRepresentation constraintAnnotation;

  @Test
  void testTickResponseDtoValidatorWhenAskPlusThenBidIsValid() {
    //given
    final var tick = Mockito.mock(TickResponseDto.class);
    //when
    Mockito.when(tick.bid()).thenReturn(1d);
    Mockito.when(tick.ask()).thenReturn(2d);
    //then
    Assertions.assertTrue(validator.isValid(tick, context));
  }

  @Test
  void testTickResponseDtoValidatorWhenAskEqualBidIsValid() {
    //given
    final var tick = Mockito.mock(TickResponseDto.class);
    //when
    Mockito.when(tick.bid()).thenReturn(1d);
    Mockito.when(tick.ask()).thenReturn(1d);
    //then
    Assertions.assertTrue(validator.isValid(tick, context));
  }

  @Test
  void testTickResponseDtoValidatorWhenAskLessThenBidIsValid() {
    //given
    final var tick = Mockito.mock(TickResponseDto.class);
    final ConstraintViolationBuilder constraintViolationBuilder = Mockito.mock(ConstraintViolationBuilder.class);
    final NodeBuilderCustomizableContext nodeBuilderCustomizableContext = Mockito.mock(NodeBuilderCustomizableContext.class);
    final String message = "anyString";
    //when
    Mockito.when(tick.bid()).thenReturn(2d);
    Mockito.when(tick.ask()).thenReturn(1d);

    Mockito.when(constraintAnnotation.message()).thenReturn(message);
    Mockito.when(context.buildConstraintViolationWithTemplate(Mockito.anyString())).thenReturn(constraintViolationBuilder);
    Mockito.when(constraintViolationBuilder.addPropertyNode(Mockito.anyString())).thenReturn(nodeBuilderCustomizableContext);
    //then
    Assertions.assertFalse(validator.isValid(tick, context));
    Mockito.verify(constraintViolationBuilder).addPropertyNode("bid");
  }
}