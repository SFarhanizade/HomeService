package ir.farhanizade.homeservice.repository.service;

import ir.farhanizade.homeservice.entity.service.MyService;
import ir.farhanizade.homeservice.repository.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

public interface ServiceRepository extends BaseRepository<MyService> {
    MyService findByName(String name);

    @Query("From MyService s Where s.parent is null")
    Page<MyService> findAllMain(Pageable pageable);

    @Query("From MyService s Where s.parent is not null")
    Page<MyService> findAllSub(Pageable pageable);
}
