package vn.com.routex.hub.notify.processor.domain.route.model;

import lombok.Builder;

@Builder
public record ProvincesCodePair(String originCode, String destinationCode) {
}
