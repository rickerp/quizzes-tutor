package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import spock.lang.Specification
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.ClarificationService

@DataJpaTest
class SubmitClarificationRequestTest extends Specification {

    @Autowired
    ClarificationService clarificationService

    def setup() {

    }

    def "submit a clarification to a question that has no clarifications"() {
    }

    def "submit a clarification to a question that already has clarifications"() {

    }

    def "submit a clarification with empty content"() {

    }

    def "submit a clarification with a question that the student didn't answer"() {

    }

    def "submit a clarification with an inconsistent question answer"() {

    }

    def "submit a clarification with an inconsistent question"() {

    }

    @TestConfiguration
    static class ClarificationServiceImplTestContextConfiguration {

        @Bean
        ClarificationService clarificationService() {
            return new ClarificationService()
        }
    }


}
