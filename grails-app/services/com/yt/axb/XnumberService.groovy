package com.yt.axb

import grails.async.Promise
import grails.gorm.DetachedCriteria
import grails.gorm.services.Service
import grails.gorm.transactions.Transactional
import grails.orm.PagedResultList
import grails.validation.ValidationException
import org.hibernate.criterion.Restrictions

import static grails.async.Promises.task

interface IXnumberService {

    Xnumber get(Serializable id)

    List<Xnumber> list(Map args)

    Long count()

    void delete(Serializable id)

    Xnumber save(Xnumber xnumber)

}

@Service(Xnumber)
abstract class XnumberService implements IXnumberService {

    private getWhere(Map args){
        def q = args.q
        def filters = args.filter

        def result = {
            if (q instanceof String){
                def qList = q.split(':')
                if (qList.size() > 1){
                    like qList[0], "%${qList[1]}%"
                }
            }

            if (q instanceof String[]){
                q.each {
                    def qList = it.split(':')
                    if (qList.size() > 1){
                        like qList[0], "%${qList[1]}%"
                    }
                }
            }

            if (filters instanceof String){
                def filtersList = filters.split(':')
                if (filtersList.size() > 1){
                    if (filtersList[0] ==~ /^.*id$/){
                        Long filterValue = filtersList[1]
                        eq filtersList[0], filterValue
                    }else if (filtersList[0] ==~ /^.*time/) {

                        Date startTime =  Date.parse("yyyy-MM-dd", filtersList[1])
                        Date endTime =  Date.parse("yyyy-MM-dd", filtersList[2])

                        if (startTime == endTime){
                            endTime = endTime.plus(1)
                        }

                        between filtersList[0], startTime, endTime
                    }else if (filtersList[0] ==~ /status/) {
                        Status status = filtersList[1]
                        if (status) {
                            eq filtersList[0], status
                        }
                    }else if (filtersList[0] ==~ /layout/) {
                        Integer layout = Integer.valueOf(filtersList[1])
                        eq filtersList[0], layout
                    }else {
                        eq filtersList[0], filtersList[1]
                    }
                }

            }

            if (filters instanceof String[]){
                filters.each {
                    def filtersList = it.split(':')
                    if (filtersList.size() > 1){
                        if (filtersList[0] ==~ /^.*id$/){
                            Long filterValue = filtersList[1]
                            eq filtersList[0], filterValue
                        }else if (filtersList[0] ==~ /^.*time/) {
                            Date startTime =  Date.parse("yyyy-MM-dd", filtersList[1])
                            Date endTime =  Date.parse("yyyy-MM-dd", filtersList[2])
                            if (startTime == endTime){
                                endTime = endTime.plus(1)
                            }
                            between filtersList[0], startTime, endTime
                        }else if (filtersList[0] ==~ /status/) {
                            Status status = filtersList[1]
                            if (status) {
                                eq filtersList[0], status
                            }
                        }else if (filtersList[0] ==~ /layout/) {
                            Integer layout = Integer.valueOf(filtersList[1])
                            eq filtersList[0], layout
                        }else {
                            eq filtersList[0], filtersList[1]
                        }
                    }

                }
            }
        }

        return result
    }

    @Transactional
    @Override
    List<Xnumber> list(Map args){
        def qList = getWhere(args)
        PagedResultList result = Xnumber.createCriteria().list(args, qList)
        return result
    }

    @Transactional
    def massInsert(List list){
        Xnumber.withTransaction{
            def accounts = []
            def total = 0
            list.each { v->
                Xnumber xnumber = new Xnumber()
                xnumber.account = v.account.trim()
                xnumber.axb_number = v.axb_number.trim()
                xnumber.layout = v.layout
                xnumber.cost = v.cost
                xnumber.appkey = v.appkey.trim()
                xnumber.secretkey = v.secretkey.trim()
                xnumber.virtualkey = v.virtualkey.trim()
                xnumber.city = v.city.trim()
                xnumber.province = v.province.trim()
                xnumber.save(flush: true)

                if (xnumber.hasErrors()){
                    accounts.add(v.axb_number.trim())
                }else {
                    total ++
                }

            }

            if (accounts.size() > 0) {
                return [code: 100, message: [errorList: accounts, total: total]]
            }
            return [code: 0, message: [total: total]]
        }
    }

    @Transactional
    def massDelete(List list){

        def axbNumbers = []
        list.each { v->
            axbNumbers.add(v.axb_number.trim())
        }

        Xnumber.withNewTransaction{
            DetachedCriteria<Xnumber> query = Xnumber.where {
                axb_number in axbNumbers
            }
            int total = query.deleteAll()

            return [code: 0, total: total]
        }
    }

    @Transactional
    def massUpdateStatus(Map args, Status status){
        def qList = getWhere(args)
        def criteria = new DetachedCriteria(Xnumber).build(qList)
        int total = criteria.updateAll(status: status)

        return total
    }

    @Transactional
    def massUpdate(Map args, Map attributes, Long max){
        def where = getWhere(args)
        List<Serializable> ids = []
        def result = Xnumber.createCriteria().list([max: max], where)

        result.each {it ->
            ids << it.id
        }
        def total = 0

        if (ids.size() > 0) {
            DetachedCriteria<Xnumber> query = Xnumber.where {
                id in ids
            }
            total = query.updateAll(attributes)
        }


        return total
    }
}