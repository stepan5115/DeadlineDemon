package sqlTables;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "admin_tokens")
@Getter @Setter
@NoArgsConstructor
public class AdminToken {
    public final static int durabilityInDays = 2;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String token;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime expiresAt = LocalDateTime.now().plusDays(durabilityInDays);;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;
}