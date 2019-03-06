package com.yt.axb

class Xnumber {

    String account
    String axb_number
    Integer layout //归属
    Float rate
    Boolean isbill = 0
    Float over
    Float overdraw
    Float cost
    Float pre_deposit
    String appkey
    String secretkey
    Double axb_number_fee
    String virtualkey
    Date create_time  = new Date()
    Date assigned_time
    Date transfer_time
    Status status = 'IDLE'

    String province
    String city

    static constraints = {
        account index: 'Account_Idx'
        axb_number unique: true, matches: '^1\\d{10}$'
        create_time nullable: true
        assigned_time nullable: true
        transfer_time nullable: true
        status nullable: true
        virtualkey nullable: true, index: 'Virtualkey_Idx'
        axb_number_fee nullable: true
        pre_deposit nullable: true
        overdraw nullable: true
        over nullable: true
        isbill nullable: true
        rate nullable: true
        appkey index: 'Appkey_Idx'
        province nullable: true
        city nullable: true
    }

    static mapping = {
        status enumType: 'string'
        sort create_time: "desc"
    }
}


/*
CREATE TABLE `axb_number` (
`id` int(11) NOT NULL AUTO_INCREMENT,
`account` varchar(255) NOT NULL DEFAULT 'api2',
`axb_number` varchar(255) NOT NULL,
`layout` int(11) DEFAULT '1' COMMENT '0:移客通，1：东信',
`rate` float(4,2) DEFAULT NULL,
`isbill` enum('1','0') DEFAULT '0',
`over` float(11,2) DEFAULT NULL,
`overdraw` float(11,2) DEFAULT NULL,
`cost` float(11,2) DEFAULT NULL,
`pre_deposit` float(11,2) DEFAULT NULL,
`appkey` varchar(255) DEFAULT NULL,
`secretkey` varchar(255) DEFAULT NULL,
`axb_number_fee` double DEFAULT '12',
`virtualkey` varchar(32) DEFAULT NULL,
`create_time` datetime DEFAULT NULL,
PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=44433 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC
*/
