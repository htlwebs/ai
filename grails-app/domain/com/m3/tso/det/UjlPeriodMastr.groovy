package com.m3.tso.det

class UjlPeriodMastr {

    String periodName;

    int month;
    int year;

    String delFlg = 'N';
    String statsFlg = 'A';
    int cretnBy;
    Date cretnDate;
    int updtdBy;
    Date updtdDate;

    static constraints = {

        periodName(nullable:false, blank: false, maxSize: 200)
        month(nullable:false, blank:false)
        year(nullable:false, blank:false)

        delFlg (nullable: false, blank: false, maxSize: 1, inList: ['Y','N'])
        statsFlg (nullable: false, blank: false, maxSize: 1, inList: ['A','I'])
        cretnBy (nullable: false, blank: false)
        cretnDate (nullable: false, blank: false)
        updtdBy (nullable: false, blank: false)
        updtdDate (nullable: true, blank: false)
    }

    static mapping = {

        id column: 'period_id';
    }
}
