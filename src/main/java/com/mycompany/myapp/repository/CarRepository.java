package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Car;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data  repository for the Car entity.
 */
@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

    @Query(value = "select distinct car from Car car left join fetch car.drivers",
        countQuery = "select count(distinct car) from Car car")
    Page<Car> findAllWithEagerRelationships(Pageable pageable);

    @Query("select distinct car from Car car left join fetch car.drivers")
    List<Car> findAllWithEagerRelationships();

    @Query("select car from Car car left join fetch car.drivers where car.id =:id")
    Optional<Car> findOneWithEagerRelationships(@Param("id") Long id);

}
