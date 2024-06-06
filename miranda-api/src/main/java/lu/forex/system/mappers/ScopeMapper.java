package lu.forex.system.mappers;

import lu.forex.system.dtos.ScopeDto;
import lu.forex.system.entities.Scope;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = ComponentModel.SPRING, uses = {SymbolMapper.class})
public interface ScopeMapper {

  Scope toEntity(ScopeDto scopeDto);

  ScopeDto toDto(Scope scope);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  Scope partialUpdate(ScopeDto scopeDto,
      @MappingTarget Scope scope);
}