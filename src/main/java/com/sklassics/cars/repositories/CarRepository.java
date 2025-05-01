package com.sklassics.cars.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sklassics.cars.entities.CarEntity;

@Repository
public interface CarRepository extends JpaRepository<CarEntity, Long> {
}
