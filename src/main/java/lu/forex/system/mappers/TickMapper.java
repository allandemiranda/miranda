package lu.forex.system.mappers;

import lu.forex.system.dtos.TickCreateDto;
import lu.forex.system.dtos.TickDto;
import lu.forex.system.entities.Tick;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = ComponentModel.SPRING, uses = {SymbolMapper.class})
public interface TickMapper {

  Tick toEntity(TickDto tickDto);

  TickDto toDto(Tick tick);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  Tick partialUpdate(TickDto tickDto, @MappingTarget Tick tick);

  Tick toEntity(TickCreateDto tickCreateDto);

  TickCreateDto toDto1(Tick tick);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  Tick partialUpdate(TickCreateDto tickCreateDto, @MappingTarget Tick tick);
}