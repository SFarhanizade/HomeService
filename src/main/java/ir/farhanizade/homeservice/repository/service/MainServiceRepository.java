package ir.farhanizade.homeservice.repository.service;

import ir.farhanizade.homeservice.entity.service.MainService;
import ir.farhanizade.homeservice.repository.BaseRepository;

public interface MainServiceRepository extends BaseRepository<MainService> {
    MainService findByName(String name);
}
