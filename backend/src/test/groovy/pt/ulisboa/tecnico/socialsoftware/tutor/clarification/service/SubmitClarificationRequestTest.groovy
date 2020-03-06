package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean


import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.ClarificationService


import spock.lang.Specification
import spock.lang.Unroll


@DataJpaTest
class SubmitClarificationRequestTest extends Specification {

    def setup() {
    }

    def "submit a clarification to a question that has no clarifications submited"() {
        expect: false
    }

    def "submit a clarification to a question that already has clarifications"() {
        expect: false
    }

    def "submit a clarification with an image"() {
        expect: false
    }

    @Unroll("Test: #creationDate | #userName | #content | #questionAnswerId | #state || #message")
    def "submit a clarification with wrong arguments"() {
        expect: false
    }

    def "submit a clarification without a creationTime"() {
        expect: false
    }

    def "submit a clarification with a question answer associated to quiz answer that is not finished"() {
        expect: false
    }

    def "submit a clarification with a question that the student didn't answer"() {
        expect: false
    }

    @TestConfiguration
    static class ClarificationServiceImplTestContextConfiguration {

        @Bean
        ClarificationService clarificationService() {
            return new ClarificationService()
        }
    }
}
