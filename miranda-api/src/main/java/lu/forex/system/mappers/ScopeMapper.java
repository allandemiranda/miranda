package lu.forex.system.mappers;

import lu.forex.system.dtos.ScopeDto;
import lu.forex.system.entities.Scope;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = ComponentModel.SPRING, uses = {SymbolMapper.class})
public interface ScopeMapper {

  Scope toEntity(ScopeDto scopeDto);

  ScopeDto toDto(Scope scope);

}