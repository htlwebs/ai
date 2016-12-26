package com.m3.tso.det

class UjlSalesDtls {

    UjlBrandMastr brand;
    UjlStcstpntMastr stcstpnt;
    int month;
    int year;
    UjlPeriodMastr period;

    Double salesVolmn;
    Double salesVal;

    String delFlg = 'N';
    String statsFlg = 'A';
    int cretnBy;
    Date cretnDate;
    int updtdBy;
    Date updtdDate;
    int version;

    static constraints = {
        brand (nullable: false, blank: false)
        stcstpnt(nullable:true, blank:false)
        month(nullable:false, blank:false)
        year(nullable:false, blank:false)
        salesVolmn(nullable: false, blank:false)
        salesVal(nullable: false, blank:false)
        period(nullable: false, blank:false)

        delFlg (nullable: false, blank: false, maxSize: 1, inList: ['Y','N'])
        statsFlg (nullable: false, blank: false, maxSize: 1, inList: ['A','I'])
        cretnBy (nullable: false, blank: false)
        cretnDate (nullable: false, blank: false)
        updtdBy (nullable: false, blank: false)
        updtdDate (nullable: true, blank: false)
    }

    static mapping = {

        id column: 'sales_dtls_id';
    }
}
