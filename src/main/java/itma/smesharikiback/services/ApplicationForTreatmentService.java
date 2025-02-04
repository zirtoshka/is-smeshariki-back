package itma.smesharikiback.services;

import itma.smesharikiback.models.*;
import itma.smesharikiback.models.reposirories.SmesharikRepository;
import itma.smesharikiback.specification.ApplicationSpecification;
import itma.smesharikiback.exceptions.GeneralException;
import itma.smesharikiback.models.reposirories.ApplicationForTreatmentRepository;
import itma.smesharikiback.models.reposirories.CommentRepository;
import itma.smesharikiback.models.reposirories.PostRepository;
import itma.smesharikiback.requests.ApplicationForTreatmentRequest;
import itma.smesharikiback.response.ApplicationForTreatmentResponse;
import itma.smesharikiback.response.PaginatedResponse;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class ApplicationForTreatmentService {
    private final CommonService commonService;
    private final ApplicationForTreatmentRepository applicationForTreatmentRepository;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final SmesharikRepository smesharikRepository;

    public ApplicationForTreatmentResponse updateApplicationForTreatment(
            Long id,
            ApplicationForTreatmentRequest applicationForTreatmentRequest
    ) {
        HashMap<String, String> map = new HashMap<>();
        commonService.checkIfDoctorOrAdmin();
        ApplicationForTreatment applicationForTreatment = getApplicationForTreatment(id);

        if (Stream.of(
                applicationForTreatmentRequest.getComment(),
                applicationForTreatmentRequest.getPost()
        ).filter(Objects::nonNull).count() != 1) {
            map.put("message", "Некорректный запрос.");
            throw new GeneralException(HttpStatus.BAD_REQUEST, map);
        }

        if (applicationForTreatmentRequest.getComment() != null) {
            Optional<Comment> comment =
                    commentRepository.findById(applicationForTreatmentRequest.getComment());
            if (comment.isEmpty()) {
                map.put("comment", "comment не был найден.");
                throw new GeneralException(HttpStatus.BAD_REQUEST, map);
            }
            applicationForTreatment.setComment(comment.get());
            applicationForTreatment.setPost(null);
        } else {
            Optional<Post> post =
                    postRepository.findById(applicationForTreatmentRequest.getPost());
            if (post.isEmpty()) {
                map.put("comment", "comment не был найден.");
                throw new GeneralException(HttpStatus.BAD_REQUEST, map);
            }
            applicationForTreatment.setPost(post.get());
            applicationForTreatment.setComment(null);
        }

        Smesharik doctor = smesharikRepository.findByLogin(applicationForTreatmentRequest.getDoctor()).orElse(null);
        if (doctor != null && !doctor.getRole().equals(SmesharikRole.DOCTOR)) {
            map.put("doctor", "Данный doctor не является доктором на самом деле.");
            throw new GeneralException(HttpStatus.BAD_REQUEST, map);
        }

        applicationForTreatment.setStatus(applicationForTreatmentRequest.getStatus());
        applicationForTreatment.setDoctor(doctor);

        return buildResponse(applicationForTreatmentRepository.save(applicationForTreatment));
    }

    public ApplicationForTreatmentResponse getApplicationForTreatmentById(Long id) {
        commonService.checkIfDoctorOrAdmin();
        ApplicationForTreatment applicationForTreatment = getApplicationForTreatment(id);
        return buildResponse(applicationForTreatment);
    }

    private ApplicationForTreatment getApplicationForTreatment(Long id) {
        Optional<ApplicationForTreatment> applicationForTreatment = applicationForTreatmentRepository.findById(id);
        if (applicationForTreatment.isEmpty()) {
            HashMap<String, String> errors = new HashMap<>();
            errors.put("message", "Такая заявка на лечение не найдена.");
            throw new GeneralException(HttpStatus.BAD_REQUEST, errors);
        }
        return applicationForTreatment.get();
    }


    public ApplicationForTreatmentResponse buildResponse(ApplicationForTreatment applicationForTreatment) {
        ApplicationForTreatmentResponse response = new ApplicationForTreatmentResponse();
        if (applicationForTreatment.getPost() != null) response.setPost(applicationForTreatment.getPost().getId());
        if (applicationForTreatment.getComment() != null) response.setComment(applicationForTreatment.getComment().getId());
        if (applicationForTreatment.getDoctor() != null) response.setDoctor(applicationForTreatment.getDoctor().getLogin());

        return response
                .setId(applicationForTreatment.getId())
                .setStatus(applicationForTreatment.getStatus())
                .setPropensities(applicationForTreatment.getPropensities().stream()
                        .map(propensity -> propensity.getPropensity().getId())
                        .collect(Collectors.toList()));

    }

    public PaginatedResponse<ApplicationForTreatmentResponse> getAll(
            List<GeneralStatus> statuses,
            Boolean isMine,
            String sortField,
            @NotNull Boolean ascending,
            @Min(value = 0) Integer page,
            @Min(value = 1) @Max(value = 50) Integer size
    ) {
        commonService.checkIfDoctorOrAdmin();
        PageRequest pageRequest = PageRequest.of(
                page,
                size,
                ascending ? Sort.by(sortField).ascending() : Sort.by(sortField).descending()
        );

        Page<ApplicationForTreatment> resultPage = applicationForTreatmentRepository.findAll(
                ApplicationSpecification.getComplaints(statuses, isMine, commonService.getCurrentSmesharik()), pageRequest);


        List<ApplicationForTreatmentResponse> content = resultPage.getContent().stream()
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
}
