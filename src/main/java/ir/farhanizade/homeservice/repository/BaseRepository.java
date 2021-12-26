package ir.farhanizade.homeservice.repository;

import ir.farhanizade.homeservice.entity.core.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BaseRepository<T extends BaseEntity> extends JpaRepository<T,Long> {
}
