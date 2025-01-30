package itma.smesharikiback.controllers;

import itma.smesharikiback.requests.ComplaintRequest;
import itma.smesharikiback.response.ComplaintResponse;
import itma.smesharikiback.response.PaginatedResponse;
import itma.smesharikiback.services.ComplaintService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/complaint")
@RequiredArgsConstructor
public class ComplaintController {
    private final ComplaintService complaintService;

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
            @RequestParam(required = false, defaultValue = "") String filter,
            @RequestParam(required = false, defaultValue = "creationDate") String sortField,
            @RequestParam(required = false, defaultValue = "true") @NotNull Boolean ascending,
            @RequestParam(required = false, defaultValue = "0") @Min(value = 0) Integer page,
            @RequestParam(required = false, defaultValue = "10") @Min(value = 1) @Max(value = 50) Integer size
    ) {
        return complaintService.getAllComplaints(filter, sortField, ascending, page, size);
    }
}
