package com.proshop.review_service.service.impl;

import com.proshop.exceptionlib.enums.ResErrorCode;
import com.proshop.exceptionlib.exceptions.ResException;
import com.proshop.review_service.dto.request.AnswerCreateRequest;
import com.proshop.review_service.dto.request.AnswerUpdateRequest;
import com.proshop.review_service.dto.request.QuestionCreateRequest;
import com.proshop.review_service.dto.request.QuestionUpdateRequest;
import com.proshop.review_service.dto.request.UpdateReviewStatusRequest;
import com.proshop.review_service.dto.response.AnswerResponse;
import com.proshop.review_service.dto.response.PageResponse;
import com.proshop.review_service.dto.response.QuestionResponse;
import com.proshop.review_service.dto.response.QuestionSummaryResponse;
import com.proshop.review_service.entity.AnswerEntity;
import com.proshop.review_service.entity.QuestionEntity;
import com.proshop.review_service.entity.ReviewCategoryEntity;
import com.proshop.review_service.entity.TagEntity;
import com.proshop.review_service.mapper.QuestionMapper;
import com.proshop.review_service.mapper.ReviewMapper;
import com.proshop.review_service.repository.AnswerRepository;
import com.proshop.review_service.repository.QuestionRepository;
import com.proshop.review_service.repository.ReviewCategoryRepository;
import com.proshop.review_service.repository.TagRepository;
import com.proshop.review_service.service.QuestionService;
import com.proshop.review_service.util.enums.ReviewStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final ReviewCategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final QuestionMapper mapper;
    private final ReviewMapper reviewMapper; // For Answer mapping

    @Override
    public QuestionResponse createQuestion(QuestionCreateRequest request, Long userId, String userName, String userAvatar) {
        log.info("Creating question for user: {}", userId);

        // Convert to entity
        QuestionEntity question = mapper.toEntity(request, userId, userName, userAvatar);
        question.setStatus(ReviewStatus.PENDING);
        question.setAnswerCount(0);
        question.setLikeCount(0);
        question.setViewCount(0);

        // Set category
        if (request.getCategoryId() != null) {
            ReviewCategoryEntity category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResException(ResErrorCode.ENTITY_NOT_EXISTS, "Category not found"));
            question.setCategory(category);
        }

        // Save question
        question = questionRepository.save(question);

        // Add tags
        if (request.getTags() != null && !request.getTags().isEmpty()) {
            List<TagEntity> tags = getOrCreateTags(request.getTags());
            question.setTags(tags);
            question = questionRepository.save(question);

            // Increment tag usage
            tags.forEach(tag -> tagRepository.incrementUsageCount(tag.getId()));
        }

        return mapper.toResponse(question);
    }

    @Override
    @Transactional
    public QuestionResponse getQuestionById(Long id) {
        QuestionEntity question = questionRepository.findById(id)
                .orElseThrow(() -> new ResException(ResErrorCode.ENTITY_NOT_EXISTS, "Question not found"));

        // Increment view count
        questionRepository.incrementViewCount(id);

        // Load answers
        List<AnswerEntity> answers = answerRepository.findByQuestion_IdOrderByCreatedAtAsc(id);

        QuestionResponse response = mapper.toResponse(question);
        response.setAnswers(reviewMapper.toAnswerResponseList(answers));

        return response;
    }

    @Override
    public QuestionResponse updateQuestion(Long id, QuestionUpdateRequest request, Long userId) {
        QuestionEntity question = questionRepository.findById(id)
                .orElseThrow(() -> new ResException(ResErrorCode.ENTITY_NOT_EXISTS, "Question not found"));

        // Check ownership
        if (!question.getUserId().equals(userId)) {
            throw new ResException(ResErrorCode.PERMISSION_DENIED, "Bạn không có quyền cập nhật câu hỏi này");
        }

        mapper.updateEntity(question, request);

        // Update category
        if (request.getCategoryId() != null) {
            ReviewCategoryEntity category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResException(ResErrorCode.ENTITY_NOT_EXISTS, "Category not found"));
            question.setCategory(category);
        }

        // Update tags
        if (request.getTags() != null) {
            // Decrement old tags
            question.getTags().forEach(tag -> tagRepository.decrementUsageCount(tag.getId()));

            // Add new tags
            List<TagEntity> newTags = getOrCreateTags(request.getTags());
            question.setTags(newTags);
            newTags.forEach(tag -> tagRepository.incrementUsageCount(tag.getId()));
        }

        question = questionRepository.save(question);

        return mapper.toResponse(question);
    }

    @Override
    public void deleteQuestion(Long id, Long userId) {
        QuestionEntity question = questionRepository.findById(id)
                .orElseThrow(() -> new ResException(ResErrorCode.ENTITY_NOT_EXISTS, "Không tìm thấy câu hỏi"));

        if (!question.getUserId().equals(userId)) {
            throw new ResException(ResErrorCode.PERMISSION_DENIED, "Bạn không có quyền xóa câu hỏi này");
        }

        // Decrement tag usage
        question.getTags().forEach(tag -> tagRepository.decrementUsageCount(tag.getId()));

        questionRepository.delete(question);
    }

    @Override
    @Transactional
    public PageResponse<QuestionSummaryResponse> getAllQuestions(Integer page, Integer size) {
        log.info("Getting all questions - page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<QuestionEntity> questionPage = questionRepository.findAll(pageable);

        return buildPageResponse(questionPage);
    }

    @Override
    @Transactional
    public PageResponse<QuestionSummaryResponse> searchQuestions(String keyword, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<QuestionEntity> questionPage = questionRepository.searchByKeyword(keyword, pageable);

        return buildPageResponse(questionPage);
    }

    @Override
    @Transactional
    public PageResponse<QuestionSummaryResponse> getQuestionsByCategory(Long categoryId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<QuestionEntity> questionPage = questionRepository.findByCategory_Id(categoryId, pageable);

        return buildPageResponse(questionPage);
    }

    @Override
    @Transactional
    public PageResponse<QuestionSummaryResponse> getQuestionsByUser(Long userId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<QuestionEntity> questionPage = questionRepository.findByUserId(userId, pageable);

        return buildPageResponse(questionPage);
    }

    @Override
    @Transactional
    public List<QuestionSummaryResponse> getHotQuestions(Integer limit) {
        Pageable pageable = PageRequest.of(0, limit);
        Page<QuestionEntity> page = questionRepository.findHotQuestions(pageable);
        return mapper.toSummaryResponseList(page.getContent());
    }

    @Override
    @Transactional
    public QuestionResponse updateQuestionStatus(Long id, UpdateReviewStatusRequest request) {
        log.info("Updating question status for questionId: {} to status: {}", id, request.getStatus());

        QuestionEntity question = questionRepository.findById(id)
                .orElseThrow(() -> new ResException(ResErrorCode.ENTITY_NOT_EXISTS, "Question not found with ID: " + id));

        ReviewStatus currentStatus = question.getStatus();
        ReviewStatus newStatus = request.getStatus();

        if (currentStatus == newStatus) {
            throw new ResException(ResErrorCode.BAD_REQUEST, "Question đã ở trạng thái: " + newStatus);
        }

        validateStatusTransition(currentStatus, newStatus);

        question.setStatus(newStatus);

        if (newStatus == ReviewStatus.REJECTED) {
            if (request.getRejectionReason() == null || request.getRejectionReason().trim().isEmpty()) {
                throw new ResException(ResErrorCode.FIELD_REQUIRED, "Lý do từ chối là bắt buộc");
            }
            question.setRejectionReason(request.getRejectionReason().trim());
        } else {
            question.setRejectionReason(null);
        }

        QuestionEntity updatedQuestion = questionRepository.save(question);

        return mapper.toResponse(updatedQuestion);
    }

    // ============================================
    // HELPER METHODS
    // ============================================

    private List<TagEntity> getOrCreateTags(List<String> tagNames) {
        List<TagEntity> tags = new ArrayList<>();

        for (String tagName : tagNames) {
            TagEntity tag = tagRepository.findByName(tagName)
                    .orElseGet(() -> {
                        TagEntity newTag = mapper.toEntity(tagName);
                        return tagRepository.save(newTag);
                    });
            tags.add(tag);
        }

        return tags;
    }

    private PageResponse<QuestionSummaryResponse> buildPageResponse(Page<QuestionEntity> page) {
        return PageResponse.<QuestionSummaryResponse>builder()
                .content(mapper.toSummaryResponseList(page.getContent()))
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .first(page.isFirst())
                .build();
    }

    private void validateStatusTransition(ReviewStatus from, ReviewStatus to) {
        if (from == ReviewStatus.CLOSED) {
            throw new ResException(ResErrorCode.BAD_REQUEST, "Không thể thay đổi trạng thái của câu hỏi đã đóng");
        }

        if (from == ReviewStatus.REJECTED && to == ReviewStatus.CLOSED) {
            throw new ResException(ResErrorCode.BAD_REQUEST, "Không thể đóng câu hỏi đã bị từ chối");
        }
    }

    // ============================================
    // ANSWER CRUD
    // ============================================

    @Override
    public AnswerResponse createAnswer(AnswerCreateRequest request, Long userId, String userName, String userAvatar) {
        QuestionEntity question = questionRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new ResException(ResErrorCode.ENTITY_NOT_EXISTS, "Question not found"));

        AnswerEntity answer = reviewMapper.toEntity(request, question, userId, userName, userAvatar);
        answer.setLikeCount(0);
        answer.setDislikeCount(0);
        answer.setIsBestAnswer(false);
        answer.setIsVerified(false);
        answer.setIsFromShop(false);
        answer = answerRepository.save(answer);

        // Increment answer count
        questionRepository.incrementAnswerCount(question.getId());

        return reviewMapper.toAnswerResponse(answer);
    }

    @Override
    public AnswerResponse updateAnswer(Long id, AnswerUpdateRequest request, Long userId) {
        AnswerEntity answer = answerRepository.findById(id)
                .orElseThrow(() -> new ResException(ResErrorCode.ENTITY_NOT_EXISTS, "Không tìm thấy câu trả lời"));

        if (!answer.getUserId().equals(userId)) {
            throw new ResException(ResErrorCode.PERMISSION_DENIED, "Bạn không có quyền cập nhật câu trả lời này");
        }

        reviewMapper.updateAnswerEntity(answer, request);
        answer = answerRepository.save(answer);

        return reviewMapper.toAnswerResponse(answer);
    }

    @Override
    public void deleteAnswer(Long id, Long userId) {
        AnswerEntity answer = answerRepository.findById(id)
                .orElseThrow(() -> new ResException(ResErrorCode.ENTITY_NOT_EXISTS, "Không tìm thấy câu trả lời"));

        if (!answer.getUserId().equals(userId)) {
            throw new ResException(ResErrorCode.PERMISSION_DENIED, "Bạn không có quyền xóa câu trả lời này");
        }

        Long questionId = answer.getQuestion().getId();
        answerRepository.delete(answer);

        // Decrement answer count
        questionRepository.decrementAnswerCount(questionId);
    }

    @Override
    @Transactional
    public List<AnswerResponse> getAnswersByQuestion(Long questionId) {
        List<AnswerEntity> answers = answerRepository.findByQuestion_IdOrderByCreatedAtAsc(questionId);
        return reviewMapper.toAnswerResponseList(answers);
    }
}
