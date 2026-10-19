package com.example.pagestock.reservation.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotNull(message = "A user must be linked to a reservation.")
    private Long userId;

    @OneToMany(mappedBy = "reservation",  cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReservationLine> lines = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    @NotNull(message = "Reservation date can not be empty.")
    private LocalDateTime reservedAt;

    @Column(nullable = true)
    private LocalDateTime pickedUpAt;

    public Reservation() {
    }

    public Reservation(Long userId, ReservationStatus status, LocalDateTime reservedAt) {
        this.userId = userId;
        this.status = status;
        this.reservedAt = reservedAt;
    }

    public void addLine(ReservationLine line) {
        lines.add(line);
        line.setReservation(this);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<ReservationLine> getLines() {
        return lines;
    }

    public void setLines(List<ReservationLine> lines) {
        this.lines = lines;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public LocalDateTime getReservedAt() {
        return reservedAt;
    }

    public void setReservedAt(LocalDateTime reservedAt) {
        this.reservedAt = reservedAt;
    }

    public LocalDateTime getPickedUpAt() {
        return pickedUpAt;
    }

    public void setPickedUpAt(LocalDateTime pickedUpAt) {
        this.pickedUpAt = pickedUpAt;
    }
}
