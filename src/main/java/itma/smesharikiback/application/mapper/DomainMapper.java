package itma.smesharikiback.application.mapper;

import itma.smesharikiback.application.dto.CommentWithChildrenDto;
import itma.smesharikiback.application.dto.PostWithCarrotsDto;
import itma.smesharikiback.domain.model.*;
import itma.smesharikiback.presentation.dto.response.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface DomainMapper {

    @Mapping(target = "author", source = "author.login")
    PostResponse toPostResponse(Post post);

    @Mapping(target = "author", source = "author.login")
    PostWithCarrotsResponse toPostWithCarrotsResponse(PostWithCarrotsDto dto);

    @Mapping(target = "smesharik", source = "smesharik.login")
    @Mapping(target = "post", source = "post.id")
    @Mapping(target = "parentComment", source = "parentComment.id")
    CommentResponse toCommentResponse(Comment comment);

    @Mapping(target = "smesharik", source = "smesharik.login")
    @Mapping(target = "post", source = "post.id")
    @Mapping(target = "parentComment", source = "parentComment.id")
    CommentWithChildrenResponse toCommentWithChildrenResponse(CommentWithChildrenDto comment);

    @Mapping(target = "smesharik", source = "smesharik.login")
    @Mapping(target = "post", source = "post.id")
    @Mapping(target = "comment", source = "comment.id")
    BanResponse toBanResponse(Ban ban);

    @Mapping(target = "admin", source = "admin.login")
    @Mapping(target = "post", source = "post.id")
    @Mapping(target = "comment", source = "comment.id")
    ComplaintResponse toComplaintResponse(Complaint complaint);

    @Mapping(target = "smesharik", source = "smesharik.login")
    @Mapping(target = "post", source = "post.id")
    @Mapping(target = "comment", source = "comment.id")
    CarrotResponse toCarrotResponse(Carrot carrot);

    @Mapping(target = "followee", source = "followee.login")
    @Mapping(target = "follower", source = "follower.login")
    FriendResponse toFriendResponse(Friend friend);

    PropensityResponse toPropensityResponse(Propensity propensity);

    @Mapping(target = "propensity", source = "propensity.id")
    TriggerWordResponse toTriggerWordResponse(TriggerWord triggerWord);

    @Mapping(target = "doctor", source = "doctor.login")
    @Mapping(target = "post", source = "post.id")
    @Mapping(target = "comment", source = "comment.id")
    @Mapping(target = "propensities", expression = "java(toPropensityIds(application))")
    ApplicationForTreatmentResponse toApplicationForTreatmentResponse(ApplicationForTreatment application);

    SmesharikResponse toSmesharikResponse(Smesharik smesharik);

    default List<Long> toPropensityIds(ApplicationForTreatment application) {
        if (application == null || application.getPropensities() == null) {
            return List.of();
        }
        return application.getPropensities().stream()
                .map(ApplicationForTreatmentPropensity::getPropensity)
                .filter(Objects::nonNull)
                .map(Propensity::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}

