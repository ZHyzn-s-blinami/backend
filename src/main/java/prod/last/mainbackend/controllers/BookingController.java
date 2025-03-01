package prod.last.mainbackend.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import prod.last.mainbackend.models.BookingModel;
import prod.last.mainbackend.models.UserModel;
import prod.last.mainbackend.models.request.BookingRequest;
import prod.last.mainbackend.services.BookingService;
import prod.last.mainbackend.services.UserService;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final UserService userService;

    @Operation(
            summary = "Создание бронирования",
            description = "Создает бронирование"
    )
    @ApiResponse(
            responseCode = "201",
            description = "Успешное создание бронирования",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookingModel.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "Ошибка при создании бронирования",
            content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"status\": false}"))
    )
    @PostMapping("/booking/create")
    public ResponseEntity<?> create(@Valid @RequestBody BookingRequest bookingRequest, Principal principal) {
        try{
            UserModel user = userService.getUserById(UUID.fromString(principal.getName()));

            return ResponseEntity.status(201).body(bookingService.create(
                    user.getId(),
                    bookingRequest.getPlaceId(),
                    bookingRequest.getStartAt(),
                    bookingRequest.getEndAt()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @Operation(
            summary = "Отмена бронирования",
            description = "Отменяет бронирование"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Успешная отмена бронирования",
            content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"status\": true}"))
    )
    @ApiResponse(
            responseCode = "404",
            description = "Бронирование не найдено",
            content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"status\": false}"))
    )
    @PostMapping("/booking/{uuid}/cancel")
    public ResponseEntity<?> cancel(@PathVariable UUID uuid) {
        try {
            bookingService.reject(uuid);
        } catch (Exception e) {
            return ResponseEntity.status(404).body("{\"status\": false}");
        }

        return ResponseEntity.ok("{\"status\": true}");
    }

    @Operation(
            summary = "Получение qr-кода бронирования",
            description = "Получает qr-код бронирования"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Успешное получение qr-кода бронирования",
            content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"qrCode\": \"code\"}"))
    )
    @ApiResponse(
            responseCode = "404",
            description = "Бронирование не найдено",
            content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"qrCode\": \"\"}"))
    )
    @GetMapping("/booking/{uuid}/qr")
    public ResponseEntity<?> qr(@PathVariable UUID uuid) {
        try {
            Map<String, String> response = new HashMap<>();
            response.put("qrCode", bookingService.generateBookingCode(uuid));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @Operation(
            summary = "Проверка qr-кода бронирования",
            description = "Проверяет qr-код бронирования"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Успешная проверка qr-кода бронирования",
            content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"status\": true}"))
    )
    @ApiResponse(
            responseCode = "404",
            description = "Бронирование не найдено",
            content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"status\": false}"))
    )
    @GetMapping("/booking/{uuid}/qr/check")
    public ResponseEntity<?> qrCheck(@PathVariable UUID uuid) {
        if (!bookingService.validateBookingCode(uuid.toString())) {
            return ResponseEntity.status(404).body("{\"status\": false}");
        }

        return ResponseEntity.ok("{\"status\": true}");
    }

    @Operation(
            summary = "Получение всех бронирований",
            description = "Получает все бронирования"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Успешное получение всех бронирований",
            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BookingModel.class)))
    )
    @ApiResponse(
            responseCode = "404",
            description = "Бронирования не найдены или любая другая ошибка. Смотреть на ошибку",
            content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"message\": \"message\"}"))
    )
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/booking/{uuid}/place")
    public ResponseEntity<?> getAllBookingByPlace(@PathVariable UUID uuid) {
        try {
            return ResponseEntity.ok(bookingService.findAllByPlaceId(uuid));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @GetMapping("/booking/{uuid}/user")
    public ResponseEntity<?> getAllBookingByUserId(@PathVariable String uuid) {
        try {
            UUID userId = UUID.fromString(uuid);

            return ResponseEntity.ok(bookingService.findAllByUserId(userId));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}
