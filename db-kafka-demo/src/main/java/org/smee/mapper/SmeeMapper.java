package org.smee.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;
import org.smee.dto.Smee;
import postgres.public$.smee.Value;

@Mapper(componentModel = MappingConstants.ComponentModel.DEFAULT)
public interface SmeeMapper {
    SmeeMapper INSTANCE = Mappers.getMapper(SmeeMapper.class);

    Smee toEntity(Value envelope);
}
