package itma.smesharikiback.presentation.controller;

import itma.smesharikiback.presentation.dto.request.PropensityRequest;
import itma.smesharikiback.presentation.dto.response.MessageResponse;
import itma.smesharikiback.presentation.dto.response.PropensityResponse;
import itma.smesharikiback.presentation.dto.response.PaginatedResponse;
import itma.smesharikiback.application.service.PropensityService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/propensity")
@AllArgsConstructor
public class PropensityController {
    private final PropensityService propensityService;

    @PostMapping
    public PropensityResponse add(
            @Validated @RequestBody PropensityRequest request
    ) {
        return propensityService.createPropensity(request);
    }

    @PutMapping("/{id}")
    public PropensityResponse update(
            @PathVariable Long id,
            @Validated @RequestBody PropensityRequest request
    ) {
        return propensityService.updatePropensity(id, request);
    }

    @DeleteMapping("/{id}")
    public MessageResponse delete(
            @PathVariable Long id
    ) {
        return propensityService.deletePropensity(id);
    }


    @GetMapping("/{id}")
    public PropensityResponse get(
            @PathVariable Long id
    ) {
        return propensityService.getPropensity(id);
    }

    @GetMapping
    public PaginatedResponse<PropensityResponse> getAll(
            @RequestParam(required = false, defaultValue = "") String filter,
            @RequestParam(required = false, defaultValue = "id") String sortField,
            @RequestParam(required = false, defaultValue = "true") @NotNull Boolean ascending,
            @RequestParam(required = false, defaultValue = "0") @Min(value = 0) Integer page,
            @RequestParam(required = false, defaultValue = "10") @Min(value = 1) @Max(value = 50) Integer size
    ) {
        return propensityService.getPropensityAll(filter, sortField, ascending, page, size);
    }
}













