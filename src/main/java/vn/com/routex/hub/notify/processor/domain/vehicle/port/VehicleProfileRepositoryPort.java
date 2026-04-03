package vn.com.routex.hub.notify.processor.domain.vehicle.port;

import vn.com.routex.hub.notify.processor.domain.common.PagedResult;
import vn.com.routex.hub.notify.processor.domain.vehicle.model.VehicleProfile;

import java.util.Optional;

public interface VehicleProfileRepositoryPort {
    boolean existsByVehiclePlate(String vehiclePlate);

    Optional<VehicleProfile> findById(String id);

    void save(VehicleProfile vehicleProfile);

    PagedResult<VehicleProfile> fetch(int pageNumber, int pageSize);
}
