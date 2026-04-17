package vn.com.routex.hub.notify.processor.application.services;

import org.springframework.data.domain.Page;
import vn.com.routex.hub.notify.processor.domain.activity.model.RecentActivity;
import vn.com.routex.hub.notify.processor.infrastructure.kafka.event.DomainEvent;

import java.time.OffsetDateTime;
import java.util.Set;

public interface RecentActivityService {

    void record(DomainEvent event);

    Page<RecentActivity> fetch(
            OffsetDateTime from,
            OffsetDateTime to,
            String merchantId,
            Set<String> eventTypes,
            int pageNumber,
            int pageSize
    );
}
