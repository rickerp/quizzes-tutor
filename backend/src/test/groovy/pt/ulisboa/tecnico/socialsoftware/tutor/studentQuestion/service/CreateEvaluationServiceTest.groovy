package pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.service

import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.EvaluationService
import spock.lang.Specification

class CreateEvaluationServiceTest extends Specification {
    def evaluationService

    def setup(){
        evaluationService = new EvaluationService()
    }

    def "studentQuestion exists and create evaluation"(){
        // the evaluation is created
        expect: false
    }

    def "studentQuestion does not exist"(){
        // an exception is thrown
        expect: false
    }

    def "studentQuestion and evaluation exist"(){
        // overwrite the evaluation
        expect: false
    }

    def "accepted is empty"(){
        // an exception is thrown
        expect: false
    }

    def "accept studentQuestion and add a justification"() {
        // an exception is thrown
        expect: false
    }

    def "reject studentQuestion and add a justification"() {
        // create evaluation
        expect: false
    }
}
