package vn.com.routex.hub.notify.processor.domain.authorities.port;


import vn.com.routex.hub.notify.processor.domain.authorities.model.UserAccountReference;

import java.util.Optional;

public interface UserAccountLookupPort {
    Optional<UserAccountReference> findById(String userId);
}
