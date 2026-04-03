package vn.com.routex.hub.notify.processor.domain.provinces.port;


import vn.com.routex.hub.notify.processor.domain.provinces.model.Province;

import java.util.Optional;

public interface ProvincesRepositoryPort {
    Optional<Province> findById(Integer id);

    boolean existsByCode(String code);

    boolean existsByName(String name);

    Province save(Province province);

    void deleteById(Integer id);
}

