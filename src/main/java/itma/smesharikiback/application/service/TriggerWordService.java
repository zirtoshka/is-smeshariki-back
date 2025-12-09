package itma.smesharikiback.application.service;

import itma.smesharikiback.infrastructure.specification.PaginationSpecification;
import itma.smesharikiback.domain.exception.ValidationException;
import itma.smesharikiback.domain.model.Propensity;
import itma.smesharikiback.domain.model.TriggerWord;
import itma.smesharikiback.domain.repository.PropensityRepository;
import itma.smesharikiback.domain.repository.TriggerWordRepository;
import itma.smesharikiback.presentation.dto.request.TriggerWordRequest;
import itma.smesharikiback.presentation.dto.response.MessageResponse;
import itma.smesharikiback.presentation.dto.response.PaginatedResponse;
import itma.smesharikiback.presentation.dto.response.TriggerWordResponse;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TriggerWordService {

    private final CommonService commonService;
    private final PropensityRepository propensityRepository;
    private TriggerWordRepository triggerWordRepository;

    public TriggerWordResponse createTriggerWord(TriggerWordRequest request) {
        commonService.checkIfAdmin();
        TriggerWord triggerWord = new TriggerWord();
        updateTriggerWordModel(triggerWord, request);
        return buildResponse(triggerWordRepository.save(triggerWord));
    }

    public TriggerWordResponse updateTriggerWord(Long id, TriggerWordRequest request) {
        commonService.checkIfAdmin();
        TriggerWord triggerWord = getTriggerWordById(id);
        updateTriggerWordModel(triggerWord, request);
        return buildResponse(triggerWordRepository.save(triggerWord));
    }

    public void updateTriggerWordModel(TriggerWord triggerWord, TriggerWordRequest request) {
        triggerWord.setWord(request.getWord());

        Optional<Propensity> propensity = propensityRepository.findById(request.getPropensity());
        if (propensity.isEmpty()) {
            HashMap<String, String> errors = new HashMap<>();
            errors.put("propensity", "Propensity не существует.");
            throw new ValidationException( errors);
        }
        triggerWord.setPropensity(propensity.get());
    }

    public MessageResponse deleteTriggerWord(Long id) {
        commonService.checkIfAdmin();
        TriggerWord TriggerWord = getTriggerWordById(id);
        triggerWordRepository.delete(TriggerWord);
        return new MessageResponse().setMessage("Наклонность удалена!");
    }

    public TriggerWordResponse getTriggerWord(Long id) {
        commonService.checkIfAdmin();
        TriggerWord TriggerWord = getTriggerWordById(id);
        return buildResponse(TriggerWord);
    }

    public PaginatedResponse<TriggerWordResponse> getTriggerWordAll(
            String filter,
            String sortField,
            Boolean ascending,
            Integer page,
            Integer size
    ) {
        commonService.checkIfAdmin();
        PageRequest pageRequest = PageRequest.of(
                page,
                size,
                ascending ? Sort.by(sortField).ascending() : Sort.by(sortField).descending()
        );

        Page<TriggerWord> resultPage = triggerWordRepository.findAll(
                PaginationSpecification.filterByMultipleFields(filter), pageRequest);


        List<TriggerWordResponse> content = resultPage.getContent().stream()
                .map(this::buildResponse)
                .collect(Collectors.toList());

        return new PaginatedResponse<>(
                content,
                resultPage.getTotalPages(),
                resultPage.getTotalElements(),
                resultPage.getNumber(),
                resultPage.getSize()
        );
    }


    private TriggerWord getTriggerWordById(Long id) {
        Optional<TriggerWord> TriggerWord = triggerWordRepository.findById(id);
        if (TriggerWord.isEmpty()) {
            HashMap<String, String> errors = new HashMap<>();
            errors.put("message", "Такая наклонность не найдена.");
            throw new ValidationException( errors);
        }
        return TriggerWord.get();
    }


    public TriggerWordResponse buildResponse(TriggerWord triggerWord) {
        TriggerWordResponse triggerWordResponse = new TriggerWordResponse();
        if (triggerWord.getPropensity() != null) triggerWordResponse.setPropensity(triggerWord.getPropensity().getId());

        return triggerWordResponse
                .setId(triggerWord.getId())
                .setWord(triggerWord.getWord());
    }
}













