package lu.forex.system.mappers;

import lu.forex.system.dtos.SymbolDto;
import lu.forex.system.dtos.SymbolUpdateDto;
import lu.forex.system.entities.Symbol;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = ComponentModel.SPRING)
public interface SymbolMapper {

  Symbol toEntity(SymbolDto symbolDto);

  SymbolDto toDto(Symbol symbol);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  Symbol partialUpdate(SymbolDto symbolDto, @MappingTarget Symbol symbol);

  Symbol toEntity(SymbolUpdateDto symbolUpdateDto);

  SymbolUpdateDto toDto1(Symbol symbol);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  Symbol partialUpdate(SymbolUpdateDto symbolUpdateDto, @MappingTarget Symbol symbol);
}