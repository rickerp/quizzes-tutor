package pt.ulisboa.tecnico.socialsoftware.tutor.administration.service

import spock.lang.Specification

class CreateStudentQuestionTest extends Specification {

    def studentQuestionService

    def "create studentQuestion with no question"() {
        // exception is thrown
        expect: false
    }

    def "create studentQuestion with already active question"() {
        // exception is thrown
        expect: false
    }

}
