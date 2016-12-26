package com.m3.tso.det

class UjlPopltnDtls {

    UjlTownMastr town;
    UjlDatapntMastr datapnt;

    Double popltnVal;

    String delFlg = 'N';
    String statsFlg = 'A';
    int cretnBy;
    Date cretnDate;
    int updtdBy;
    Date updtdDate;
    int version;

    static constraints = {
        town (nullable: false, blank: false)
        datapnt(nullable:true, blank:false)
        popltnVal(nullable: false, blank:false)

        delFlg (nullable: false, blank: false, maxSize: 1, inList: ['Y','N'])
        statsFlg (nullable: false, blank: false, maxSize: 1, inList: ['A','I'])
        cretnBy (nullable: false, blank: false)
        cretnDate (nullable: false, blank: false)
        updtdBy (nullable: false, blank: false)
        updtdDate (nullable: true, blank: false)
    }

    static mapping = {

        id column: 'popltn_dtls_id';
    }
}
