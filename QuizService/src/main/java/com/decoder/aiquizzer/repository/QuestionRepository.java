package com.decoder.aiquizzer.repository;

import com.decoder.aiquizzer.entity.Question;
import com.decoder.aiquizzer.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findByQuiz(Quiz quiz);

}
