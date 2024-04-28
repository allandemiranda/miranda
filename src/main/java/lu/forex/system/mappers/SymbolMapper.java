package lu.forex.system.mappers;

import lu.forex.system.dtos.SymbolDto;
import lu.forex.system.dtos.SymbolResponseDto;
import lu.forex.system.entities.Symbol;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = ComponentModel.SPRING)
public interface SymbolMapper {

  Symbol toEntity(SymbolDto symbolDto);

  SymbolResponseDto toDto(Symbol symbol);
}