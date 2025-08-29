package org.smee.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.smee.dto.Smee;
import org.smee.dto.Smee2;
import postgres.public$.smee.Value;

@Mapper
public interface SmeeMapper {
    SmeeMapper INSTANCE = Mappers.getMapper(SmeeMapper.class);

    Smee toEntity(Value envelope);

    Smee2 toEntity(postgres.public$.smee2.Value envelope);
}
