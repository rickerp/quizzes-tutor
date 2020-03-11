import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration

import spock.lang.Specification

@DataJpaTest
class SubmitCommentTest extends Specification {

    def setup() {

    }

    def "submit a comment to a clarification request"() {

    }

    def "submit comment to an non existing clarification request"() {

    }

    def "submit a comment to a resolved clarification request" () {

    }

    def "submit comment with empty content"() {

    }

    def "submit comment with wrong arguments"() {

    }

    def "submit an empty comment"() {

    }

    def "student tries to submit comment to clarification request"() {

    }

    def "teacher tries to submits comment to clarification request from different course"() {

    }

    @TestConfiguration
    static class SubmitCommentTestContextConfiguration {

    }
}