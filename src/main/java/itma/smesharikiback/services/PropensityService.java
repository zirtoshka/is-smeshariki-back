package itma.smesharikiback.services;

import itma.smesharikiback.config.PaginationSpecification;
import itma.smesharikiback.exceptions.GeneralException;
import itma.smesharikiback.models.Propensity;
import itma.smesharikiback.models.reposirories.PropensityRepository;
import itma.smesharikiback.requests.PropensityRequest;
import itma.smesharikiback.response.MessageResponse;
import itma.smesharikiback.response.PaginatedResponse;
import itma.smesharikiback.response.PropensityResponse;
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
public class PropensityService {

    private final CommonService commonService;
    private PropensityRepository propensityRepository;

    public PropensityResponse createPropensity(PropensityRequest request) {
        commonService.checkIfAdmin();
        Propensity propensity = new Propensity();
        propensity.setName(request.getName());
        propensity.setDescription(request.getDescription());
        return buildResponse(propensityRepository.save(propensity));
    }

    public PropensityResponse updatePropensity(Long id, PropensityRequest request) {
        commonService.checkIfAdmin();
        Propensity propensity = getPropensityById(id);
        propensity.setName(request.getName());
        propensity.setDescription(request.getDescription());
        return buildResponse(propensityRepository.save(propensity));
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
        return buildResponse(propensity);
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


    private Propensity getPropensityById(Long id) {
        Optional<Propensity> propensity = propensityRepository.findById(id);
        if (propensity.isEmpty()) {
            HashMap<String, String> errors = new HashMap<>();
            errors.put("message", "Такая наклонность не найдена.");
            throw new GeneralException(HttpStatus.BAD_REQUEST, errors);
        }
        return propensity.get();
    }


    public PropensityResponse buildResponse(Propensity propensity) {
        return new PropensityResponse()
                .setId(propensity.getId())
                .setDescription(propensity.getDescription())
                .setName(propensity.getName());
    }
}
