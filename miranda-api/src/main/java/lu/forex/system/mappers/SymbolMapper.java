package lu.forex.system.mappers;

import lu.forex.system.dtos.SymbolCreateDto;
import lu.forex.system.dtos.SymbolResponseDto;
import lu.forex.system.entities.Symbol;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = ComponentModel.SPRING)
public interface SymbolMapper {

  Symbol toEntity(SymbolCreateDto symbolCreateDto);

  SymbolResponseDto toDto(Symbol symbol);

  Symbol toEntity(SymbolResponseDto symbolResponseDto);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  Symbol partialUpdate(SymbolResponseDto symbolResponseDto,
      @MappingTarget Symbol symbol);
}