package vn.com.routex.hub.notify.processor.domain.route.port;


import vn.com.routex.hub.notify.processor.domain.route.model.ProvincesCodePair;

public interface RouteProvincesLookupPort {
    ProvincesCodePair getCodes(String origin, String destination);
}
