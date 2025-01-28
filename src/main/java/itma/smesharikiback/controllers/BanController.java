package itma.smesharikiback.controllers;

import itma.smesharikiback.requests.BanRequest;
import itma.smesharikiback.response.BanResponse;
import itma.smesharikiback.response.CarrotResponse;
import itma.smesharikiback.response.MessageResponse;
import itma.smesharikiback.response.PaginatedResponse;
import itma.smesharikiback.services.BanService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = "/api/ban")
@RequiredArgsConstructor
public class BanController {

    private final BanService banService;

    @PostMapping
    public BanResponse add(
            @Validated @RequestBody BanRequest request
    ) {
        return banService.create(request);
    }

    @DeleteMapping("/{id}")
    public MessageResponse delete(
            @PathVariable(required = false) Long id
    ) {
        return banService.delete(id);
    }

    @PutMapping("/{id}")
    public BanResponse update(
            @PathVariable(required = false) Long id,
            @Validated @RequestBody BanRequest request
    ) {
        return banService.update(id, request);
    }

    @GetMapping("/{id}")
    public BanResponse get(
            @PathVariable(required = false) Long id
    ) {
        return banService.get(id);
    }

    @GetMapping
    public PaginatedResponse<BanResponse> getAll(
            @RequestParam(required = false, defaultValue = "") String filter,
            @RequestParam(required = false, defaultValue = "creationDate") String sortField,
            @RequestParam(required = false, defaultValue = "true") @NotNull Boolean ascending,
            @RequestParam(required = false, defaultValue = "0") @Min(value = 0) Integer page,
            @RequestParam(required = false, defaultValue = "10") @Min(value = 1) @Max(value = 50) Integer size
    ) {
        return banService.getAll(filter, sortField, ascending, page, size);
    }

}
