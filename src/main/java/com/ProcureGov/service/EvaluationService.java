package com.ProcureGov.service;

import com.ProcureGov.repository.EvaluationRepository;

public class EvaluationService {
    private final EvaluationRepository evaluationRepository;

    public EvaluationService() {
        this.evaluationRepository = new EvaluationRepository();
    }

    public int getActiveEvaluationCount() throws Exception {
        return evaluationRepository.getActiveEvaluationCount();
    }

    public int getCompletedEvaluationCount()  throws Exception {
        return evaluationRepository.getCompletedEvaluationCount();
    }

    public double getAverageEvaluationScore() throws Exception {
        return evaluationRepository.getAverageEvaluationScore();
    }
}
