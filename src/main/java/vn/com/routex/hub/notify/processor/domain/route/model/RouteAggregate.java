package vn.com.routex.hub.notify.processor.domain.route.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import vn.com.routex.hub.notify.processor.domain.auditing.AbstractAuditingEntity;
import vn.com.routex.hub.notify.processor.domain.route.RouteStatus;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class RouteAggregate extends AbstractAuditingEntity {
    private String id;
    private String creator;
    private String merchantId;
    private String originName;
    private String destinationName;
    private String originCode;
    private String destinationCode;
    private Long duration;
    private RouteStatus status;
    private List<RouteStopPlan> stopPlans;

    public static RouteAggregate plan(
            String id,
            String creator,
            String merchantId,
            String originCode,
            String destinationCode,
            String originName,
            String destinationName,
            Long duration,
            OffsetDateTime createdAt,
            List<RouteStopPlan> stopPlans
    ) {
        return RouteAggregate.builder()
                .id(id)
                .creator(creator)
                .merchantId(merchantId)
                .originCode(originCode)
                .destinationCode(destinationCode)
                .originName(originName)
                .destinationName(destinationName)
                .duration(duration)
                .status(RouteStatus.ACTIVE)
                .createdAt(createdAt)
                .createdBy(creator)
                .stopPlans(stopPlans == null ? new ArrayList<>() : new ArrayList<>(stopPlans))
                .build();
    }

    public void cancel(String actor, OffsetDateTime updatedAt) {
        this.status = RouteStatus.INACTIVE;
        this.setCreatedBy(actor);
        this.setUpdatedAt(updatedAt);
    }
}
