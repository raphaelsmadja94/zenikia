package com.zenika.zenikia.cv.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * A smaller local model (see README "Run for free with Ollama") doesn't always honor a
 * nested-object schema as strictly as OpenAI's structured outputs — these tests lock in that the
 * mapping tolerates a list item coming back as a bare string instead of a full object.
 */
class CvAnalysisAiResponseTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void deserializes_wellFormedObjectsNormally() throws Exception {
        String json = """
                {
                  "role": "Senior Java Developer",
                  "experienceYears": 8,
                  "skills": [{"name": "Kafka", "category": "DATA_MESSAGING", "claimedLevel": "ADVANCED"}],
                  "claims": [{"statement": "Migration Kafka", "relatedSkills": ["Kafka"], "challengeableConcepts": ["scalabilité"]}],
                  "experiences": [{"title": "Tech Lead", "company": "ExampleCorp", "period": "2021-2024", "summary": "Migration"}]
                }
                """;

        CvAnalysisAiResponse response = mapper.readValue(json, CvAnalysisAiResponse.class);

        assertThat(response.skills()).hasSize(1);
        assertThat(response.skills().get(0).name()).isEqualTo("Kafka");
        assertThat(response.skills().get(0).category()).isEqualTo("DATA_MESSAGING");
        assertThat(response.claims().get(0).challengeableConcepts()).containsExactly("scalabilité");
        assertThat(response.experiences().get(0).company()).isEqualTo("ExampleCorp");
    }

    @Test
    void tolerates_bareStringsInPlaceOfObjects_asASmallerModelMightReturn() throws Exception {
        String json = """
                {
                  "role": "Développeur",
                  "experienceYears": 5,
                  "skills": ["Java", "Spring Boot"],
                  "claims": ["A mis en place une architecture microservices"],
                  "experiences": ["Développeur chez ExampleCorp"]
                }
                """;

        CvAnalysisAiResponse response = mapper.readValue(json, CvAnalysisAiResponse.class);

        assertThat(response.skills()).extracting(CvAnalysisAiResponse.SkillAi::name).containsExactly("Java", "Spring Boot");
        assertThat(response.skills().get(0).category()).isNull();
        assertThat(response.claims().get(0).statement()).isEqualTo("A mis en place une architecture microservices");
        assertThat(response.claims().get(0).relatedSkills()).isEmpty();
        assertThat(response.experiences().get(0).title()).isEqualTo("Développeur chez ExampleCorp");
    }

    @Test
    void tolerates_aSingleStringWhereAStringArrayWasExpected() throws Exception {
        String json = """
                {
                  "role": "Développeur",
                  "experienceYears": null,
                  "skills": [],
                  "claims": [{"statement": "Claim", "relatedSkills": "Kafka", "challengeableConcepts": "scalabilité"}],
                  "experiences": []
                }
                """;

        CvAnalysisAiResponse response = mapper.readValue(json, CvAnalysisAiResponse.class);

        assertThat(response.claims().get(0).relatedSkills()).containsExactly("Kafka");
        assertThat(response.claims().get(0).challengeableConcepts()).containsExactly("scalabilité");
    }
}
