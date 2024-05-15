package lu.forex.system.validators;

import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext;
import lu.forex.system.annotations.TickRepresentation;
import lu.forex.system.dtos.TickCreateDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TickCreateDtoValidatorTest {

  @Mock
  private ConstraintValidatorContext context;

  @InjectMocks
  private TickCreateDtoValidator validator;

  @Mock
  private TickRepresentation constraintAnnotation;

  @Test
  void testTickCreateDtoValidatorWhenAskPlusThenBidIsValid() {
    //given
    final var tick = Mockito.mock(TickCreateDto.class);
    //when
    Mockito.when(tick.bid()).thenReturn(1d);
    Mockito.when(tick.ask()).thenReturn(2d);
    //then
    Assertions.assertTrue(validator.isValid(tick, context));
  }

  @Test
  void testTickCreateDtoValidatorWhenAskEqualBidIsValid() {
    //given
    final var tick = Mockito.mock(TickCreateDto.class);
    //when
    Mockito.when(tick.bid()).thenReturn(1d);
    Mockito.when(tick.ask()).thenReturn(1d);
    //then
    Assertions.assertTrue(validator.isValid(tick, context));
  }

  @Test
  void testTickCreateDtoValidatorWhenAskLessThenBidIsValid() {
    //given
    final var tick = Mockito.mock(TickCreateDto.class);
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