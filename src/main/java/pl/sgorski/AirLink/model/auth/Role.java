package pl.sgorski.AirLink.model.auth;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;
import java.util.Set;

@Table(name = "roles")
@Entity
@Data
public class Role implements GrantedAuthority, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min = 3, max = 100, message = "Role name must be between 3 and 100 characters")
    @Column(unique = true, nullable = false)
    private String name;

    @Override
    public String getAuthority() {
        return this.name;
    }

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(this.name);
    }
}
