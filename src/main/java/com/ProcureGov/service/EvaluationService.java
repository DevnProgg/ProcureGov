package com.ProcureGov.service;

import com.ProcureGov.repository.EvaluationRepository;

public class EvaluationService {
    private final EvaluationRepository evaluationRepository;

    public EvaluationService() {
        this.evaluationRepository = new EvaluationRepository();
    }

    public int getActiveEvaluationCount() {
        return evaluationRepository.getActiveEvaluationCount();
    }

    public int getCompletedEvaluationCount() {
        return evaluationRepository.getCompletedEvaluationCount();
    }

    public double getAverageEvaluationScore() {
        return evaluationRepository.getAverageEvaluationScore();
    }
}
