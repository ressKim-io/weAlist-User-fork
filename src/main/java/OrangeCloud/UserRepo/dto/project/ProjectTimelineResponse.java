// ProjectTimelineResponse.java
package OrangeCloud.UserRepo.dto.project;

import java.time.LocalDateTime;
import java.util.List;

public class ProjectTimelineResponse {
    private Long projectId;
    private List<TimelineEvent> events;

    // 기본 생성자
    public ProjectTimelineResponse() {}

    // 모든 필드 생성자 - 명시적으로 추가
    public ProjectTimelineResponse(Long projectId, List<TimelineEvent> events) {
        this.projectId = projectId;
        this.events = events;
    }

    // Getter, Setter
    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public List<TimelineEvent> getEvents() { return events; }
    public void setEvents(List<TimelineEvent> events) { this.events = events; }

    // 내부 클래스 TimelineEvent
    public static class TimelineEvent {
        private String eventType;
        private String description;
        private String performedBy;
        private LocalDateTime timestamp;

        // 기본 생성자
        public TimelineEvent() {}

        // 모든 필드 생성자 - 명시적으로 추가
        public TimelineEvent(String eventType, String description, String performedBy, LocalDateTime timestamp) {
            this.eventType = eventType;
            this.description = description;
            this.performedBy = performedBy;
            this.timestamp = timestamp;
        }

        // Getter, Setter
        public String getEventType() { return eventType; }
        public void setEventType(String eventType) { this.eventType = eventType; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getPerformedBy() { return performedBy; }
        public void setPerformedBy(String performedBy) { this.performedBy = performedBy; }

        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

        @Override
        public String toString() {
            return "TimelineEvent{" +
                    "eventType='" + eventType + '\'' +
                    ", description='" + description + '\'' +
                    ", performedBy='" + performedBy + '\'' +
                    ", timestamp=" + timestamp +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "ProjectTimelineResponse{" +
                "projectId=" + projectId +
                ", events=" + events +
                '}';
    }
}