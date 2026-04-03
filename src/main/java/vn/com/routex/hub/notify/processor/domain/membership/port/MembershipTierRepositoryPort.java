package vn.com.routex.hub.notify.processor.domain.membership.port;


import vn.com.routex.hub.notify.processor.domain.membership.model.MembershipTier;

import java.util.Optional;

public interface MembershipTierRepositoryPort {
    Optional<MembershipTier> findById(String id);

    Optional<MembershipTier> findByPriorityLevel(int i);
}
