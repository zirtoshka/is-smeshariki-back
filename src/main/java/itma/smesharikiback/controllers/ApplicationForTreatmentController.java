package itma.smesharikiback.controllers;

import itma.smesharikiback.requests.ApplicationForTreatmentRequest;
import itma.smesharikiback.response.ApplicationForTreatmentResponse;
import itma.smesharikiback.response.PaginatedResponse;
import itma.smesharikiback.services.ApplicationForTreatmentService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/application")
@RequiredArgsConstructor
public class ApplicationForTreatmentController {
    private final ApplicationForTreatmentService applicationForTreatmentService;

    @PutMapping("/{id}")
    public ApplicationForTreatmentResponse update(
            @PathVariable Long id,
            @Validated @RequestBody ApplicationForTreatmentRequest request
    ) {
        return applicationForTreatmentService.updateApplicationForTreatment(id, request);
    }

    @GetMapping("/{id}")
    public ApplicationForTreatmentResponse get(
            @PathVariable Long id
    ) {
        return applicationForTreatmentService.getApplicationForTreatmentById(id);
    }

    @GetMapping
    public PaginatedResponse<ApplicationForTreatmentResponse> getAll(
            @RequestParam(required = false, defaultValue = "") String filter,
            @RequestParam(required = false, defaultValue = "id") String sortField,
            @RequestParam(required = false, defaultValue = "true") @NotNull Boolean ascending,
            @RequestParam(required = false, defaultValue = "0") @Min(value = 0) Integer page,
            @RequestParam(required = false, defaultValue = "10") @Min(value = 1) @Max(value = 50) Integer size
    ) {
        return applicationForTreatmentService.getAll(filter, sortField, ascending, page, size);
    }
}
