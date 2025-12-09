package itma.smesharikiback.application.service;

import itma.smesharikiback.application.mapper.DomainMapper;
import itma.smesharikiback.domain.exception.DomainException;
import itma.smesharikiback.domain.exception.ValidationException;
import itma.smesharikiback.domain.model.*;
import itma.smesharikiback.domain.repository.ApplicationForTreatmentRepository;
import itma.smesharikiback.domain.repository.CommentRepository;
import itma.smesharikiback.domain.repository.PostRepository;
import itma.smesharikiback.domain.repository.SmesharikRepository;
import itma.smesharikiback.infrastructure.specification.ApplicationSpecification;
import itma.smesharikiback.presentation.dto.request.ApplicationForTreatmentRequest;
import itma.smesharikiback.presentation.dto.response.ApplicationForTreatmentResponse;
import itma.smesharikiback.presentation.dto.response.PaginatedResponse;
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
    private final DomainMapper domainMapper;

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
            map.put("message", "Exactly one of comment or post must be set.");
            throw new ValidationException(map);
        }

        if (applicationForTreatmentRequest.getComment() != null) {
            Optional<Comment> comment =
                    commentRepository.findById(applicationForTreatmentRequest.getComment());
            if (comment.isEmpty()) {
                map.put("comment", "Comment not found.");
                throw new ValidationException(map);
            }
            applicationForTreatment.setComment(comment.get());
            applicationForTreatment.setPost(null);
        } else {
            Optional<Post> post =
                    postRepository.findById(applicationForTreatmentRequest.getPost());
            if (post.isEmpty()) {
                map.put("comment", "Comment not found.");
                throw new ValidationException(map);
            }
            applicationForTreatment.setPost(post.get());
            applicationForTreatment.setComment(null);
        }

        Smesharik doctor = smesharikRepository.findByLogin(applicationForTreatmentRequest.getDoctor()).orElse(null);
        if (doctor != null && !doctor.getRole().equals(SmesharikRole.DOCTOR)) {
            map.put("doctor", "Doctor must have DOCTOR role.");
            throw new ValidationException(map);
        }

        applicationForTreatment.setStatus(applicationForTreatmentRequest.getStatus());
        applicationForTreatment.setDoctor(doctor);

        return domainMapper.toApplicationForTreatmentResponse(applicationForTreatmentRepository.save(applicationForTreatment));
    }

    public ApplicationForTreatmentResponse getApplicationForTreatmentById(Long id) {
        commonService.checkIfDoctorOrAdmin();
        ApplicationForTreatment applicationForTreatment = getApplicationForTreatment(id);
        return domainMapper.toApplicationForTreatmentResponse(applicationForTreatment);
    }

    private ApplicationForTreatment getApplicationForTreatment(Long id) {
        Optional<ApplicationForTreatment> applicationForTreatment = applicationForTreatmentRepository.findById(id);
        if (applicationForTreatment.isEmpty()) {
            HashMap<String, String> errors = new HashMap<>();
            errors.put("message", "Application not found.");
            throw new DomainException(HttpStatus.NOT_FOUND, errors);
        }
        return applicationForTreatment.get();
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
                .map(domainMapper::toApplicationForTreatmentResponse)
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

