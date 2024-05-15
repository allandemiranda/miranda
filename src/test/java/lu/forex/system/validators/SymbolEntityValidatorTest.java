package lu.forex.system.validators;

import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext;
import java.util.Arrays;
import lu.forex.system.annotations.SymbolCurrencyRepresentation;
import lu.forex.system.entities.Symbol;
import lu.forex.system.enums.Currency;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SymbolEntityValidatorTest {

  @Mock
  private ConstraintValidatorContext context;

  @InjectMocks
  private SymbolEntityValidator validator;

  @Mock
  private SymbolCurrencyRepresentation constraintAnnotation;

  @Mock
  private ConstraintViolationBuilder constraintViolationBuilder;

  @Mock
  private NodeBuilderCustomizableContext nodeBuilderCustomizableContext;

  @BeforeEach
  void init() {
    Mockito.when(constraintAnnotation.message()).thenReturn("message");
    Mockito.when(context.buildConstraintViolationWithTemplate(Mockito.anyString())).thenReturn(constraintViolationBuilder);
    Mockito.when(constraintViolationBuilder.addPropertyNode(Mockito.anyString())).thenReturn(nodeBuilderCustomizableContext);
  }

  @ParameterizedTest
  @EnumSource(Currency.class)
  void testSymbolEntityValidatorWhenCurrencyIsDiffIsValid(Currency currency) {
    //given
    final var symbol = Mockito.mock(Symbol.class);
    //when
    Mockito.when(symbol.getCurrencyBase()).thenReturn(currency);
    Mockito.when(symbol.getCurrencyQuote()).thenReturn(Arrays.stream(Currency.values()).filter(c -> !currency.equals(c)).findFirst().orElse(null));
    //then
    Assertions.assertTrue(validator.isValid(symbol, context));
  }

  @ParameterizedTest
  @EnumSource(Currency.class)
  void testSymbolEntityValidatorWhenCurrencyIsEqualIsInvalid(Currency currency) {
    //given
    final var symbol = Mockito.mock(Symbol.class);
    //when
    Mockito.when(symbol.getCurrencyBase()).thenReturn(currency);
    Mockito.when(symbol.getCurrencyQuote()).thenReturn(currency);
    //then
    Assertions.assertFalse(validator.isValid(symbol, context));
    Mockito.verify(constraintViolationBuilder).addPropertyNode("currencyBase");
  }
}