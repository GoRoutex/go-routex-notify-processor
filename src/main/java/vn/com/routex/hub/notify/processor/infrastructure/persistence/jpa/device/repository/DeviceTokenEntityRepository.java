package vn.com.routex.hub.notify.processor.infrastructure.persistence.jpa.device.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.com.routex.hub.notify.processor.infrastructure.persistence.jpa.device.entity.DeviceTokenEntity;

import java.util.List;

@Repository
public interface DeviceTokenEntityRepository extends JpaRepository<DeviceTokenEntity, String> {
    @Query("""
    select d.fcmToken
    from DeviceTokenEntity d
    where d.driverId = :driverId
      and d.active = true
  """)
    List<String> findActiveFcmTokensByDriverId(String driverId);
}
