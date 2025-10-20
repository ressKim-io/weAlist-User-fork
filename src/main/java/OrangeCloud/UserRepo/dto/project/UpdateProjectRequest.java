// UpdateProjectRequest.java
package OrangeCloud.UserRepo.dto.project;

public class UpdateProjectRequest {
    private String status;
    private String description;

    // 기본 생성자
    public UpdateProjectRequest() {}

    // 생성자
    public UpdateProjectRequest(String status, String description) {
        this.status = status;
        this.description = description;
    }

    // Getter, Setter
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}