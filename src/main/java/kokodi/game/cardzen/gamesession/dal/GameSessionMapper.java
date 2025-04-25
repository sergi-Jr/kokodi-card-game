package kokodi.game.cardzen.gamesession.dal;

import kokodi.game.cardzen.gamesession.dto.GameSessionDto;
import kokodi.game.cardzen.gamesession.dto.GameSessionStorageInfoDto;
import kokodi.game.cardzen.gamesession.model.GameSession;
import kokodi.game.cardzen.gamesession.model.GameSessionStorage;
import kokodi.game.cardzen.mapper.JsonNullableMapper;
import kokodi.game.cardzen.mapper.ReferenceMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(uses = {JsonNullableMapper.class, ReferenceMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class GameSessionMapper {
    public abstract GameSession toEntity(GameSessionDto gameSessionDto);

    public abstract GameSessionDto toGameSessionDto(GameSession gameSession);

    @Mapping(target = "playersWithPoints", source = "userIdsWithPoints")
    public abstract GameSessionStorageInfoDto toStorageInfoDto(GameSessionStorage model);
}
