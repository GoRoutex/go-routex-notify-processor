package vn.com.routex.hub.notify.processor.application.reminder;


import org.springframework.stereotype.Component;
import vn.com.routex.hub.notify.processor.infrastructure.kafka.event.RouteAssignedEvent;

@Component
public class ReminderScheduler {

    public void scheduleRouteReminders(RouteAssignedEvent payload) {

    }
}
