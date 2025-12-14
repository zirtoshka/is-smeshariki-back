package itma.smesharikiback.presentation.controller;

import itma.smesharikiback.domain.model.GeneralStatus;
import itma.smesharikiback.presentation.dto.request.ComplaintRequest;
import itma.smesharikiback.presentation.dto.response.ComplaintResponse;
import itma.smesharikiback.presentation.dto.response.PaginatedResponse;
import itma.smesharikiback.application.service.ComplaintService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/complaint")
@RequiredArgsConstructor
public class ComplaintController {
    private final ComplaintService complaintService;
    private static final List<GeneralStatus> DEFAULT_STATUSES = Arrays.asList(
            GeneralStatus.NEW,
            GeneralStatus.DONE,
            GeneralStatus.IN_PROGRESS,
            GeneralStatus.CANCELED
    );

    @PostMapping
    public ComplaintResponse add(
            @Validated @RequestBody ComplaintRequest request
    ) {
        return complaintService.createComplaint(request);
    }

    @PutMapping("/{id}")
    public ComplaintResponse update(
            @PathVariable Long id,
            @Validated @RequestBody ComplaintRequest request
    ) {
        return complaintService.updateComplaint(id, request);
    }

    @GetMapping("/{id}")
    public ComplaintResponse get(
            @PathVariable Long id
    ) {
        return complaintService.getComplaint(id);
    }

    @GetMapping
    public PaginatedResponse<ComplaintResponse> getAll(
            @RequestParam(required = false, defaultValue = "") String description,
            @RequestParam(required = false) List<GeneralStatus> statuses,
            @RequestParam(required = false) Boolean isMine,
            @RequestParam(required = false, defaultValue = "creationDate") String sortField,
            @RequestParam(required = false, defaultValue = "true") @NotNull Boolean ascending,
            @RequestParam(required = false, defaultValue = "0") @Min(value = 0) Integer page,
            @RequestParam(required = false, defaultValue = "10") @Min(value = 1) @Max(value = 50) Integer size
    ) {
        List<GeneralStatus> normalizedStatuses = statuses == null ? List.of() : statuses.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (normalizedStatuses.isEmpty()) {
            normalizedStatuses = DEFAULT_STATUSES;
        }

        return complaintService.getAllComplaints(description, normalizedStatuses, isMine, sortField, ascending, page, size);
    }
}













