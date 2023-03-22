package com.mysite.sbb;

import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.question.Question;
import com.mysite.sbb.answer.AnswerRepository;
import com.mysite.sbb.question.QuestionRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SbbApplicationTests {

	@Autowired
	private QuestionRepository questionRepository;

	@Autowired
	private AnswerRepository answerRepository;

	@BeforeEach //아래 메서드는 각 테스트케이스가 실행되기 전에 실행된다.
	void beforeEach() {
		// 모든 데이터 삭제
		answerRepository.deleteAll();
		answerRepository.clearAutoIncrement();
		//모든 데이터 삭제
		questionRepository.deleteAll();

		//흔적삭제(다음번 INSERT때 id가 1번으로 설정되도록)
		questionRepository.clearAutoIncrement();

		Question q1 = new Question();
		q1.setSubject("sbb가 무엇인가요?");
		q1.setContent("sbb에 대해서 알고 싶습니다.");
		q1.setCreateDate(LocalDateTime.now());
		this.questionRepository.save(q1); // 첫번째 질문 저장

		Question q2 = new Question();
		q2.setSubject("스프링부트 모델 질문입니다.");
		q2.setContent("id는 자동으로 생성되나요?");
		q2.setCreateDate(LocalDateTime.now());
		this.questionRepository.save(q2); // 두번째 질문 저장

		// 답변 1개 생성
		Answer a1 = new Answer();
		a1.setContent("네 자동으로 생성됩니다.");
		q2.addAnswer(a1);
		a1.setCreateDate(LocalDateTime.now());
		answerRepository.save(a1);

	}

	@Test
	@DisplayName("데이터 저장")
	void t001() {
		Question q = new Question();
		q.setSubject("세계에서 가장 부유한 국가가 어디인가요?");
		q.setContent("알고 싶습니다");
		q.setCreateDate(LocalDateTime.now());
		questionRepository.save(q);

		assertEquals("세계에서 가장 부유한 국가가 어디인가요?", questionRepository.findById(3).get().getSubject());

	}

	@Test
	@DisplayName("데이터 조회")
	void t002() {
		List<Question> all = this.questionRepository.findAll();
		assertEquals(2, all.size());

		Question q = all.get(0);
		assertEquals("sbb가 무엇인가요?", q.getSubject());
	}

	@Test
	@DisplayName("findById")
	void t003() {
		Optional<Question> oq = this.questionRepository.findById(1);
		if (oq.isPresent()) {
			Question q = oq.get();
			assertEquals("sbb가 무엇인가요?", q.getSubject());
		}
	}

	@Test
	@DisplayName("findBySubject")
	void t004() {
		Question q = this.questionRepository.findBySubject("sbb가 무엇인가요?");
		assertEquals(1, q.getId());
	}

	@Test
	@DisplayName("findBySubjectAndContent")
	void t005() {
		Question q = this.questionRepository.findBySubjectAndContent(
				"sbb가 무엇인가요?", "sbb에 대해서 알고 싶습니다."
		);
		assertEquals(1, q.getId());
	}

	@Test
	@DisplayName("findBySubjectLike")
	void t006() {
		List<Question> qList = this.questionRepository.findBySubjectLike("sbb%");
		Question q = qList.get(0);
		assertEquals("sbb가 무엇인가요?", q.getSubject());
	}

	@Test
	@DisplayName("데이터 수정")
	void t007() {
		Optional<Question> oq = questionRepository.findById(1);
		assertTrue(oq.isPresent());
		Question q = oq.get();
		q.setSubject("수정된 제목");
		questionRepository.save(q);
	}

	@Test
	@DisplayName("데이터 삭제")
	void t008() {
		assertEquals(2, questionRepository.count());
		Optional<Question> oq = questionRepository.findById(1);
		assertTrue(oq.isPresent());
		Question q = oq.get();
		questionRepository.delete(q);
		assertEquals(1, questionRepository.count());
	}

	@Test
	@DisplayName("답변 데이터 생성 저장")
	void t009() {
		Optional<Question> oq = questionRepository.findById(2);
		assertTrue(oq.isPresent());
		Question q = oq.get();

		Answer a = new Answer();
		a.setContent("네 자동으로 생성됩니다.");
		a.setQuestion(q);
		a.setCreateDate(LocalDateTime.now());
		answerRepository.save(a);
	}

	@Test
	@DisplayName("답변 조회")
	void t010() {
		Optional<Answer> oa = answerRepository.findById(1);
		assertTrue(oa.isPresent());
		Answer a = oa.get();
		assertEquals(2, a.getQuestion().getId());
	}

	@Transactional //여기서의 트랜잭션의 역할 : 함수가 끝날 때까지 전화(DB와의)를 끊지 않음
	//@Rollback(false)
	@Test
	@DisplayName("답변에 달린 질문 찾기")
	void t011() {
		Optional<Question> oq = questionRepository.findById(2);
		assertTrue(oq.isPresent());
		Question q = oq.get();

		List<Answer> answerList = q.getAnswerList();

		assertEquals(1, answerList.size());
		assertEquals("네 자동으로 생성됩니다.", answerList.get(0).getContent());

	}
}
