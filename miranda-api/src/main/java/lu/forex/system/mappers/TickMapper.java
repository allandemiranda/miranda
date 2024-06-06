package lu.forex.system.mappers;

import lu.forex.system.dtos.NewTickDto;
import lu.forex.system.dtos.TickDto;
import lu.forex.system.entities.Symbol;
import lu.forex.system.entities.Tick;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = ComponentModel.SPRING, uses = {SymbolMapper.class})
public interface TickMapper {

  @Mapping(source = "symbol", target = "symbol")
  Tick toEntity(NewTickDto newTickDto, Symbol symbol);

  TickDto toDto(Tick tick);
}