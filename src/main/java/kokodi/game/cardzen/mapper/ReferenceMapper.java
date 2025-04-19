package kokodi.game.cardzen.mapper;

import jakarta.persistence.EntityManager;
import kokodi.game.cardzen.model.BaseEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.TargetType;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING
)
public abstract class ReferenceMapper {
    @Autowired
    private EntityManager manager;

    public <T extends BaseEntity> T toEntity(Long id, @TargetType Class<T> clazz) {
        return id != null ? manager.find(clazz, id) : null;
    }
}
