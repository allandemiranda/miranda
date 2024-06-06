package lu.forex.system.mappers;

import lu.forex.system.dtos.NewSymbolDto;
import lu.forex.system.dtos.ResponseSymbolDto;
import lu.forex.system.entities.Symbol;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = ComponentModel.SPRING)
public interface SymbolMapper {

  @Mapping(source = "swapShort", target = "swap.percentageShort")
  @Mapping(source = "swapLong", target = "swap.percentageLong")
  @Mapping(source = "currencyQuote", target = "currencyPair.quote")
  @Mapping(source = "currencyBase", target = "currencyPair.base")
  Symbol toEntity(NewSymbolDto newSymbolDto);

  ResponseSymbolDto toDto(Symbol symbol);

}