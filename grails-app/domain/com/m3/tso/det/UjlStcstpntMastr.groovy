package com.m3.tso.det

class UjlStcstpntMastr {

    String stcstpntName;
    String stcstpntCd;

    UjlStcstpntTypeMastr stcstpntType;
    String censCityName;

    double latVal = 0.00;
    double lngVal = 0.00;

    UjlStateMastr state;
    UjlDistrctMastr distrct;
    UjlStcstpntMastr parntStcstpnt;
    UjlTownMastr censTown;
    String pinCode;

    String delFlg = 'N';
    String statsFlg = 'A';
    int cretnBy;
    Date cretnDate;
    int updtdBy;
    Date updtdDate;
    int version;

    static constraints = {
        stcstpntName (nullable: false, blank: false, maxSize: 200)
        stcstpntCd (nullable: true, blank: true, maxSize: 50)

        stcstpntType (nullable: false, blank: false)
        censCityName (nullable: false, blank: false , maxSize: 200)
        parntStcstpnt (nullable: true, blank: false)
        censTown(nullable: true, blank:false)
        pinCode(nullable: false)

        latVal(nullable: false, blank: false)
        lngVal(nullable: false, blank: false)

        state (nullable: false, blank: false)
        distrct (nullable: false, blank: false)

        delFlg (nullable: false, blank: false, maxSize: 1, inList: ['Y','N'])
        statsFlg (nullable: false, blank: false, maxSize: 1, inList: ['A','I'])
        cretnBy (nullable: false, blank: false)
        cretnDate (nullable: false, blank: false)
        updtdBy (nullable: false, blank: false)
        updtdDate (nullable: true, blank: false)
    }

    static mapping = {

        id column: 'stcstpnt_id';
    }
}
