package prod.last.mainbackend.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "palce")
public class PlaceModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private PlaceType type;

    @NotNull
    private Integer capacity;

    @NotBlank
    private String description;

    @NotNull
    private LocalDateTime createdAt;

    public PlaceModel() {}

    public PlaceModel(Integer capacity, String description, PlaceType type) {
        this.createdAt = LocalDateTime.now();
        this.capacity = capacity;
        this.description = description;
        this.type = type;
    }
}
