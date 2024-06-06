package lu.forex.system.mappers;

import lu.forex.system.dtos.NewSymbolDto;
import lu.forex.system.dtos.SymbolDto;
import lu.forex.system.entities.Symbol;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = ComponentModel.SPRING)
public interface SymbolMapper {

  @Mapping(source = "swapShort", target = "swap.percentageShort")
  @Mapping(source = "swapLong", target = "swap.percentageLong")
  @Mapping(source = "currencyQuote", target = "currencyPair.quote")
  @Mapping(source = "currencyBase", target = "currencyPair.base")
  Symbol toEntity(NewSymbolDto newSymbolDto);


  Symbol toEntity(SymbolDto symbolDto);

  SymbolDto toDto(Symbol symbol);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  Symbol partialUpdate(SymbolDto symbolDto,
      @MappingTarget Symbol symbol);
}