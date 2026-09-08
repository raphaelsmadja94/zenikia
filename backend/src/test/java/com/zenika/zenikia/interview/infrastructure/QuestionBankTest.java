package com.zenika.zenikia.interview.infrastructure;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class QuestionBankTest {

    private final QuestionBank bank = new QuestionBank(new DefaultResourceLoader());

    @Test
    void examplesFor_matchesASkillNameThatDoesNotExactlyEqualTheBankKey() {
        // "Spring Boot" as extracted from a CV should still match the "spring" bucket.
        List<String> examples = bank.examplesFor("Spring Boot", 3);

        assertThat(examples).isNotEmpty();
    }

    @Test
    void examplesFor_fallsBackToCraftQuestionsForAnUnknownSkill() {
        List<String> examples = bank.examplesFor("CobolLegacySystem2000", 3);

        assertThat(examples).isNotEmpty();
    }

    @Test
    void examplesFor_neverReturnsMoreThanTheRequestedLimit() {
        List<String> examples = bank.examplesFor("Java", 2);

        assertThat(examples).hasSizeLessThanOrEqualTo(2);
    }

    @Test
    void postureQuestions_returnsBehavioralQuestionsForRecruiter() {
        List<String> examples = bank.postureQuestions(3);

        assertThat(examples).isNotEmpty();
        assertThat(examples).allSatisfy(q -> assertThat(q).isNotBlank());
    }
}
