package itma.smesharikiback.controllers;

import itma.smesharikiback.requests.TriggerWordRequest;
import itma.smesharikiback.response.MessageResponse;
import itma.smesharikiback.response.PaginatedResponse;
import itma.smesharikiback.response.TriggerWordResponse;
import itma.smesharikiback.services.TriggerWordService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/word")
@AllArgsConstructor
public class TriggerWordController {
    private final TriggerWordService triggerWordService;

    @PostMapping
    public TriggerWordResponse add(
            @Validated @RequestBody TriggerWordRequest request
    ) {
        return triggerWordService.createTriggerWord(request);
    }

    @PutMapping("/{id}")
    public TriggerWordResponse update(
            @PathVariable Long id,
            @Validated @RequestBody TriggerWordRequest request
    ) {
        return triggerWordService.updateTriggerWord(id, request);
    }

    @DeleteMapping("/{id}")
    public MessageResponse delete(
            @PathVariable Long id
    ) {
        return triggerWordService.deleteTriggerWord(id);
    }


    @GetMapping("/{id}")
    public TriggerWordResponse get(
            @PathVariable Long id
    ) {
        return triggerWordService.getTriggerWord(id);
    }

    @GetMapping
    public PaginatedResponse<TriggerWordResponse> getAll(
            @RequestParam(required = false, defaultValue = "") String filter,
            @RequestParam(required = false, defaultValue = "id") String sortField,
            @RequestParam(required = false, defaultValue = "true") @NotNull Boolean ascending,
            @RequestParam(required = false, defaultValue = "0") @Min(value = 0) Integer page,
            @RequestParam(required = false, defaultValue = "10") @Min(value = 1) @Max(value = 50) Integer size
    ) {
        return triggerWordService.getTriggerWordAll(filter, sortField, ascending, page, size);
    }
}
