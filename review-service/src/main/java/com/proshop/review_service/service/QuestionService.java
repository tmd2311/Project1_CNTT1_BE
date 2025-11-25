package com.proshop.review_service.service;

import com.proshop.review_service.dto.request.AnswerCreateRequest;
import com.proshop.review_service.dto.request.AnswerUpdateRequest;
import com.proshop.review_service.dto.request.QuestionCreateRequest;
import com.proshop.review_service.dto.request.QuestionUpdateRequest;
import com.proshop.review_service.dto.request.UpdateReviewStatusRequest;
import com.proshop.review_service.dto.response.AnswerResponse;
import com.proshop.review_service.dto.response.PageResponse;
import com.proshop.review_service.dto.response.QuestionResponse;
import com.proshop.review_service.dto.response.QuestionSummaryResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Service interface cho QuestionEntity (Q&A)
 */
public interface QuestionService {

    // CRUD
    QuestionResponse createQuestion(QuestionCreateRequest request, Long userId, String userName, String userAvatar);
    QuestionResponse getQuestionById(Long id);
    QuestionResponse updateQuestion(Long id, QuestionUpdateRequest request, Long userId);
    void deleteQuestion(Long id, Long userId);

    // Listing & Search
    PageResponse<QuestionSummaryResponse> getAllQuestions(Integer page, Integer size);
    PageResponse<QuestionSummaryResponse> searchQuestions(String keyword, Integer page, Integer size);
    PageResponse<QuestionSummaryResponse> getQuestionsByCategory(Long categoryId, Integer page, Integer size);
    PageResponse<QuestionSummaryResponse> getQuestionsByUser(Long userId, Integer page, Integer size);
    List<QuestionSummaryResponse> getHotQuestions(Integer limit);

    // Status management
    QuestionResponse updateQuestionStatus(Long id, UpdateReviewStatusRequest request);

    // Answer management
    AnswerResponse createAnswer(AnswerCreateRequest request, Long userId, String userName, String userAvatar);
    AnswerResponse updateAnswer(Long id, AnswerUpdateRequest request, Long userId);
    void deleteAnswer(Long id, Long userId);
    List<AnswerResponse> getAnswersByQuestion(Long questionId);
}
