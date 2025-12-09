package itma.smesharikiback.application.service;

import itma.smesharikiback.application.mapper.DomainMapper;
import itma.smesharikiback.infrastructure.specification.PaginationSpecification;
import itma.smesharikiback.domain.exception.ValidationException;
import itma.smesharikiback.domain.model.Propensity;
import itma.smesharikiback.domain.repository.PropensityRepository;
import itma.smesharikiback.presentation.dto.request.PropensityRequest;
import itma.smesharikiback.presentation.dto.response.MessageResponse;
import itma.smesharikiback.presentation.dto.response.PaginatedResponse;
import itma.smesharikiback.presentation.dto.response.PropensityResponse;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PropensityService {

    private final CommonService commonService;
    private final PropensityRepository propensityRepository;
    private final DomainMapper domainMapper;

    public PropensityResponse createPropensity(PropensityRequest request) {
        commonService.checkIfAdmin();
        Propensity propensity = new Propensity();
        propensity.setName(request.getName());
        propensity.setDescription(request.getDescription());
        return domainMapper.toPropensityResponse(propensityRepository.save(propensity));
    }

    public PropensityResponse updatePropensity(Long id, PropensityRequest request) {
        commonService.checkIfAdmin();
        Propensity propensity = getPropensityById(id);
        propensity.setName(request.getName());
        propensity.setDescription(request.getDescription());
        return domainMapper.toPropensityResponse(propensityRepository.save(propensity));
    }

    public MessageResponse deletePropensity(Long id) {
        commonService.checkIfAdmin();
        Propensity propensity = getPropensityById(id);
        propensityRepository.delete(propensity);
        return new MessageResponse().setMessage("Наклонность удалена!");
    }

    public PropensityResponse getPropensity(Long id) {
        commonService.checkIfAdmin();
        Propensity propensity = getPropensityById(id);
        return domainMapper.toPropensityResponse(propensity);
    }

    public PaginatedResponse<PropensityResponse> getPropensityAll(
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

        Page<Propensity> resultPage = propensityRepository.findAll(
                PaginationSpecification.filterByMultipleFields(filter), pageRequest);


        List<PropensityResponse> content = resultPage.getContent().stream()
                .map(domainMapper::toPropensityResponse)
                .collect(Collectors.toList());

        return new PaginatedResponse<>(
                content,
                resultPage.getTotalPages(),
                resultPage.getTotalElements(),
                resultPage.getNumber(),
                resultPage.getSize()
        );
    }


    private Propensity getPropensityById(Long id) {
        Optional<Propensity> propensity = propensityRepository.findById(id);
        if (propensity.isEmpty()) {
            HashMap<String, String> errors = new HashMap<>();
            errors.put("message", "Наклонность не найдена.");
            throw new ValidationException(errors);
        }
        return propensity.get();
    }
}
