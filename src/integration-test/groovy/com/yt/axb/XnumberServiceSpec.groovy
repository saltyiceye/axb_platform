package com.yt.axb

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification
import org.hibernate.SessionFactory

@Integration
@Rollback
class XnumberServiceSpec extends Specification {

    XnumberService xnumberService
    SessionFactory sessionFactory

    private Long setupData() {
        // TODO: Populate valid domain instances and return a valid ID
        //new Xnumber(...).save(flush: true, failOnError: true)
        //new Xnumber(...).save(flush: true, failOnError: true)
        //Xnumber xnumber = new Xnumber(...).save(flush: true, failOnError: true)
        //new Xnumber(...).save(flush: true, failOnError: true)
        //new Xnumber(...).save(flush: true, failOnError: true)
        assert false, "TODO: Provide a setupData() implementation for this generated test suite"
        //xnumber.id
    }

    void "test get"() {
        setupData()

        expect:
        xnumberService.get(1) != null
    }

    void "test list"() {
        setupData()

        when:
        List<Xnumber> xnumberList = xnumberService.list(max: 2, offset: 2)

        then:
        xnumberList.size() == 2
        assert false, "TODO: Verify the correct instances are returned"
    }

    void "test count"() {
        setupData()

        expect:
        xnumberService.count() == 5
    }

    void "test delete"() {
        Long xnumberId = setupData()

        expect:
        xnumberService.count() == 5

        when:
        xnumberService.delete(xnumberId)
        sessionFactory.currentSession.flush()

        then:
        xnumberService.count() == 4
    }

    void "test save"() {
        when:
        assert false, "TODO: Provide a valid instance to save"
        Xnumber xnumber = new Xnumber()
        xnumberService.save(xnumber)

        then:
        xnumber.id != null
    }
}
