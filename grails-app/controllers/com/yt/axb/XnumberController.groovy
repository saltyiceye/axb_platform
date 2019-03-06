package com.yt.axb

import grails.converters.JSON
import grails.gorm.DetachedCriteria
import grails.validation.ValidationException
import org.apache.poi.ss.usermodel.*
import static org.grails.plugins.excelimport.ExpectedPropertyType.StringType
import static org.springframework.http.HttpStatus.*

class XnumberController {

    XnumberService xnumberService

    def excelImportService

    static responseFormats = ['json', 'xml']
    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        //respond xnumberService.list(params), model:[xnumberCount: xnumberService.count()]
        def result = xnumberService.list(params)
        //respond result.resultList, model: [customerCount: result.totalCount]
        [list: result.resultList, count: result.totalCount]
    }

    def show(Long id) {
        respond xnumberService.get(id)
    }

    def save() {
        def axbNumbers = request.JSON.axb_numbers
        def errorList = []

        axbNumbers.each{
            def xnumber = new Xnumber()
            bindData(xnumber, request.JSON, [exclude: ["axb_number"]])
            xnumber.axb_number = it
            try {
                xnumberService.save(xnumber)
            } catch (ValidationException e) {
                def er = xnumber.errors
                errorList.add(it)
            }
        }

        respond([code: 0, message: [errorList: errorList]])

        //respond xnumber, [status: CREATED, view:"show"]
    }

    def update(Xnumber xnumber) {
        if (xnumber == null) {
            render status: NOT_FOUND
            return
        }

        try {
            xnumberService.save(xnumber)
        } catch (ValidationException e) {
            respond xnumber.errors, view:'edit'
            return
        }

        respond xnumber, [status: OK, view:"show"]
    }

    def delete(Long id) {
        if (id == null) {
            render status: NOT_FOUND
            return
        }

        xnumberService.delete(id)

        render status: NO_CONTENT
    }

    def massUpdate(){
        def requestJson = request.JSON
        def attributes = requestJson.attributes
        Long max = 0
        if (requestJson.limit){
            max = Long.valueOf(requestJson.limit)
        }

        if ("appkey" in attributes.keySet() || "virtualkey" in attributes.keySet()){
            attributes.assigned_time = new Date()
        }

        if ("status" in attributes.keySet()){
            Status status = Status.valueOf(attributes.status)
            attributes.status = status
        }

        if (requestJson.ids) {
            List<Serializable> ids = []

            requestJson.ids.each {
                ids << Long.valueOf(it)
            }

            int total = Xnumber.where {
                id in ids
            }.updateAll(attributes)

            respond([code: 0, message: [total: total]])
            return
        }

        int total = xnumberService.massUpdate(params, attributes, max)
        respond([code: 0, message: [total: total]])
    }

    def updateStatus(){
        def requestJson = request.JSON
        Status status = Status.valueOf(requestJson.attributes.status)
        if (requestJson.ids) {
            List<Serializable> ids = []

            requestJson.ids.each {
                ids << Long.valueOf(it)
            }

            try {
                Xnumber.executeUpdate("update Xnumber c set status = :status where c.id in :ids", [status: status, ids: ids])
            } catch (ValidationException e) {
                respond([code: 500, message:"Internal server error"])
                return
            }
            respond([code: 0, message:"success"])
            return
        }

        int total = xnumberService.massUpdateStatus(params, status)
        respond([code: 0, message: [total: total]])
    }

    def massTransfer() {
        def requestJson = request.JSON
        if (requestJson.ids) {
            List<Serializable> ids = []
            requestJson.ids.each {
                ids << Long.valueOf(it)
            }

            try {
                Xnumber.executeUpdate("update Xnumber c set virtualkey = :virtualkey, transfer_time = :transferTime where c.id in :ids", [virtualkey: requestJson.virtualkey, transferTime: new Date(), ids: ids])
            } catch (ValidationException e) {
                respond([code: 500, message: "Internal server error"])
                return
            }

            respond([code: 0, message: "success"])
            return
        }

        int total = xnumberService.massUpdateStatus(params, status)
        respond([code: 0, message: [total: total]])
    }

    def massUpload() {
        InputStream inputStream = null
        try {
            Map CONFIG_BOOK_COLUMN_MAP = [
                    sheet: 'Sheet1',
                    startRow: 1,
                    columnMap:  [
                            'A': 'account',
                            'B': 'axb_number',
                            "C": 'layout',
                            "D": 'cost',
                            "E": 'appkey',
                            "F": 'secretkey',
                            "G": 'virtualkey',
                            "H": 'province',
                            "I": 'city'
                    ]
            ]

            def f = request.getFile('uploadFile')
            //inputStream = grailsApplication.getParentContext().getResource("classpath:$filePath").getInputStream()
            inputStream = f.getInputStream()
            Workbook workbook = WorkbookFactory.create(inputStream)
            def configParam = [
                    account: [expectedType: StringType],
                    axb_number: [expectedType: StringType],
                    //layout: [expectedType: StringType],
                    //cost: [expectedType: StringType],
                    appkey: [expectedType: StringType],
                    secretkey: [expectedType: StringType],
                    virtualkey: [expectedType: StringType],
                    province: [expectedType: StringType],
                    city: [expectedType: StringType],
            ]
            List list = excelImportService.columns(workbook, CONFIG_BOOK_COLUMN_MAP, null, configParam)
            def insertMessage = xnumberService.massInsert(list)
            respond(insertMessage)
        } catch (Exception e) {
            e.printStackTrace()
            respond([code: 500, message: "Internal Server Error"])
        }finally{
            if(!inputStream){
                inputStream.close()
            }
        }
    }

    def massDelete(){
        InputStream inputStream = null
        try {
            Map CONFIG_BOOK_COLUMN_MAP = [
                    sheet: 'Sheet1',
                    startRow: 1,
                    columnMap:  [
                            'A': 'axb_number'
                    ]
            ]

            def f = request.getFile('uploadFile')
            //inputStream = grailsApplication.getParentContext().getResource("classpath:$filePath").getInputStream()
            inputStream = f.getInputStream()
            Workbook workbook = WorkbookFactory.create(inputStream)
            def configParam = [
                    axb_number: [expectedType: StringType]
            ]
            List list = excelImportService.columns(workbook, CONFIG_BOOK_COLUMN_MAP, null, configParam)
            def message = xnumberService.massDelete(list)
            respond(message)
        } catch (Exception e) {
            e.printStackTrace()
            respond([code: 500, message: "Internal Server Error"])
        }finally{
            if(!inputStream){
                inputStream.close()
            }
        }
    }
}
