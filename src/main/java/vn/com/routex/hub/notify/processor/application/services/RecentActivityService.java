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
            String audienceType,
            String scopeType,
            String scopeId,
            String merchantId,
            Set<String> eventTypes,
            String severity,
            String status,
            String sourceService,
            String entityType,
            String entityId,
            String actorUserId,
            String keyword,
            int pageNumber,
            int pageSize
    );
}
