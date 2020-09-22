package com.space.controller;

import com.space.controller.status.BadRequestException;
import com.space.controller.status.NotFoundShipException;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@RestController
@RequestMapping(path = "/rest/")
public class ShipController {
    private final ShipRepository repository;

    @Autowired
    public ShipController(ShipRepository repository) {
        this.repository = repository;
    }

    @GetMapping("ships")
    public List<Ship> findByPagingCriteria(@RequestParam Map<String, String> pathVars) {
        return getPage(pathVars, repository).getContent();
    }


    @GetMapping("ships/count")
    public Long countShips(@RequestParam Map<String, String> pathVars) {
        return getPage(pathVars, repository).getTotalElements();
    }

    private static Page<Ship> getPage(Map<String, String> pathVars, ShipRepository repository) {
        Map<String, String> vars = new HashMap<>(pathVars);

        final int pageNumber = Integer.parseInt(vars.getOrDefault("pageNumber", "0"));
        final int pageSize = Integer.parseInt(vars.getOrDefault("pageSize", "3"));
        final String order = vars.getOrDefault("order", "ID").toLowerCase();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(order));

        Specification<Ship> spec =
        new Specification<Ship>() {
            @Override
            public Predicate toPredicate(Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                if(vars.containsKey("name")) {
                    predicates.add(cb.and(cb.like(root.get("name"), "%" + vars.get("name") + "%")));
                }

                if(vars.containsKey("planet")) {
                    predicates.add(cb.and(cb.like(root.get("planet"), "%" + vars.get("planet") + "%")));
                }

                if(vars.containsKey("maxCrewSize")) {
                    predicates.add(cb.and(cb.le(root.get("crewSize"), Integer.parseInt(vars.get("maxCrewSize")))));
                }

                if(vars.containsKey("maxSpeed")) {
                    predicates.add(cb.and(cb.le(root.get("speed"), Double.parseDouble(vars.get("maxSpeed")))));
                }

                if(vars.containsKey("minSpeed")) {
                    predicates.add(cb.and(cb.ge(root.get("speed"), Double.parseDouble(vars.get("minSpeed")))));
                }

                if(vars.containsKey("maxRating")) {
                    predicates.add(cb.and(cb.le(root.get("rating"), Double.parseDouble(vars.get("maxRating")))));
                }

                if(vars.containsKey("minRating")) {
                    predicates.add(cb.and(cb.ge(root.get("rating"), Double.parseDouble(vars.get("minRating")))));
                }

                if(vars.containsKey("minCrewSize")) {
                    predicates.add(cb.and(cb.ge(root.get("crewSize"), Integer.parseInt(vars.get("minCrewSize")))));
                }

                if(vars.containsKey("shipType")) {
                    predicates.add(cb.and(cb.equal(root.get("shipType"), ShipType.valueOf(vars.get("shipType")))));
                }

                if(vars.containsKey("isUsed")) {
                    predicates.add(cb.and(cb.equal(root.get("isUsed"), Boolean.valueOf(vars.get("isUsed")))));
                }

                if(vars.containsKey("before")) {
                    Date param = new Date(Long.parseLong(vars.get("before"))) ;
                    predicates.add(cb.and(cb.lessThanOrEqualTo(root.get("prodDate"), param)));
                }

                if(vars.containsKey("after")) {
                    Date param = new Date(Long.parseLong(vars.get("after"))) ;
                    predicates.add(cb.and(cb.greaterThanOrEqualTo(root.get("prodDate"), param)));
                }

                return cb.and(predicates.toArray(new Predicate[0]));
            }
        };

        Page<Ship> page = repository.findAll(spec, pageable);

        return page;
    }

    @GetMapping("ships/{id}")
    public Ship getById(@PathVariable(value = "id") Long shipId) {
        if (shipId <= 0)
            throw new BadRequestException();

        Optional<Ship> oShip = repository.findById(shipId);

        if(oShip.isPresent()) {
            return oShip.get();
        }
        else {
            throw new NotFoundShipException("not found Ship with id: " + shipId);
        }
    }

    @DeleteMapping("ships/{id}")
    public ResponseEntity<String> deleteShip(@PathVariable(value = "id") Long shipId) {
        ResponseEntity<String> result;

        if (shipId <= 0)
            throw new BadRequestException();

        try {
            if (repository.existsById(shipId)) {
                repository.deleteById(shipId);
                result = new ResponseEntity<>("Ok", HttpStatus.OK);
            } else {
                result = new ResponseEntity<>("Not found ship with id: " + shipId, HttpStatus.NOT_FOUND);
            }
        } catch (HttpMessageConversionException e) {
            throw new BadRequestException();
        }

        return result;
    }

    @PostMapping(value = "ships", consumes = "application/json", produces = "application/json")
    public Ship createShip(@RequestBody Ship ship) {

        if(ship.getName() == null || ship.getName().isEmpty() || ship.getName().length() > 50)
            throw new BadRequestException();

        if(ship.getPlanet() == null || ship.getPlanet().isEmpty() || ship.getPlanet().length() > 50)
            throw new BadRequestException();

        if(ship.getSpeed() == null) {
            throw new BadRequestException();
        }

        ship.setSpeed(BigDecimal.valueOf(ship.getSpeed()).setScale(2, RoundingMode.HALF_DOWN).doubleValue());
        if(ship.getSpeed() < 0.1 || ship.getSpeed() > 0.99)
            throw new BadRequestException();

        if(ship.getCrewSize() == null || ship.getCrewSize() < 1 || ship.getCrewSize() > 9999)
            throw new BadRequestException();

        if(ship.isUsed() == null)
            ship.setUsed(false);

        if(ship.getProdDate() == null)
            throw new BadRequestException();

        if(ship.getProdDate().getYear() < (2800 - 1900) || ship.getProdDate().getYear() > (3019 - 1900))
            throw new BadRequestException();

        if(ship.getShipType() == null)
            throw new BadRequestException();

        double rating = BigDecimal.valueOf((80.0 * ship.getSpeed() * (ship.isUsed()?0.5:1.0))/(1119.0 - ship.getProdDate().getYear() + 1.0))
                .setScale(2, RoundingMode.HALF_DOWN).doubleValue();

        ship.setRating(rating);

        repository.save(ship);

        return ship;
    }


    @PostMapping(value = "ships/{id}", consumes = "application/json", produces = "application/json")
    public Ship updateShip(@RequestBody Ship ship, @PathVariable Long id)  {
        if(id <= 0)
            throw new BadRequestException();

        if(!repository.existsById(id))
            throw new NotFoundShipException();

        Ship shipForEdit = repository.findById(id).get();

        if(ship.getName() != null) {
            if (ship.getName().isEmpty() || ship.getName().length() > 50) {
                throw new BadRequestException();
            }
            else {
                shipForEdit.setName(ship.getName());
            }
        }

        if(ship.getPlanet() != null) {
            if(ship.getPlanet().isEmpty() || ship.getPlanet().length() > 50) {
                throw new BadRequestException();
            }
            else {
                shipForEdit.setPlanet(ship.getPlanet());
            }
        }

        if(ship.getCrewSize() != null) {
            if (ship.getCrewSize() < 1 || ship.getCrewSize() > 9999) {
                throw new BadRequestException();
            } else {
                shipForEdit.setCrewSize(ship.getCrewSize());
            }
        }

        if(ship.getProdDate() != null) {
            if (ship.getProdDate().getYear() < (2800 - 1900) || ship.getProdDate().getYear() > (3019 - 1900)) {
                throw new BadRequestException();
            } else {
                shipForEdit.setProdDate(ship.getProdDate());
            }
        }

        if(ship.getShipType() != null) {
            shipForEdit.setShipType(ship.getShipType());
        }

        if(ship.getSpeed() != null) {
            double speed = BigDecimal.valueOf(ship.getSpeed()).setScale(2, RoundingMode.HALF_DOWN).doubleValue();
            if(speed < 0.1 || speed > 0.99)
                throw new BadRequestException();
            shipForEdit.setSpeed(speed);
        }

        if(ship.isUsed() != null) {
            shipForEdit.setUsed(ship.isUsed());
        }

        double rating =
                BigDecimal.valueOf(
                (80.0 * shipForEdit.getSpeed() * (shipForEdit.isUsed()?0.5:1.0))
                        / (1119.0 - shipForEdit.getProdDate().getYear() + 1.0))
                .setScale(2, RoundingMode.HALF_DOWN).doubleValue();

        shipForEdit.setRating(rating);

        repository.save(shipForEdit);

        return shipForEdit;
    }
}
