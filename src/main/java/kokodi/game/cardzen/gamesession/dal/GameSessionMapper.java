package kokodi.game.cardzen.gamesession.dal;

import kokodi.game.cardzen.gamesession.dto.GameSessionDto;
import kokodi.game.cardzen.gamesession.model.GameSession;
import kokodi.game.cardzen.mapper.JsonNullableMapper;
import kokodi.game.cardzen.mapper.ReferenceMapper;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(uses = {JsonNullableMapper.class, ReferenceMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class GameSessionMapper {
    public abstract GameSession toEntity(GameSessionDto gameSessionDto);

    public abstract GameSessionDto toGameSessionDto(GameSession gameSession);
}
