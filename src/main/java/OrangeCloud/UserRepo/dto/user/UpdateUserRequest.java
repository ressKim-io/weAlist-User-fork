// UpdateUserRequest.java
package OrangeCloud.UserRepo.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public class UpdateUserRequest {

    @Size(min = 2, max = 50, message = "이름은 2자 이상 50자 이하여야 합니다.")
    private String name;

    @Email(message = "올바른 이메일 형식이어야 합니다.")
    private String email;

    @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.")
    private String password;

    private String currentPassword; // 비밀번호 변경 시 현재 비밀번호 확인용

    // 기본 생성자
    public UpdateUserRequest() {}

    // 매개변수가 있는 생성자
    public UpdateUserRequest(String name, String email, String password, String currentPassword) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.currentPassword = currentPassword;
    }

    // Getter 메서드들
    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    // Setter 메서드들
    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    // toString 메서드 (디버깅용)
    @Override
    public String toString() {
        return "UpdateUserRequest{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='[PROTECTED]'" +
                ", currentPassword='[PROTECTED]'" +
                '}';
    }

    // equals 메서드
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        UpdateUserRequest that = (UpdateUserRequest) obj;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (email != null ? !email.equals(that.email) : that.email != null) return false;
        if (password != null ? !password.equals(that.password) : that.password != null) return false;
        return currentPassword != null ? currentPassword.equals(that.currentPassword) : that.currentPassword == null;
    }

    // hashCode 메서드
    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (currentPassword != null ? currentPassword.hashCode() : 0);
        return result;
    }
}