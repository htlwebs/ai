package com.m3.tso.det

class UjlDistrctMastr {

    String distrctName;
    UjlStateMastr state;
    double latVal = 0.00;
    double lngVal = 0.00;
    int totPopltn = 0;

    String delFlg = 'N';
    String statsFlg = 'A';
    int cretnBy;
    Date cretnDate;
    int updtdBy;
    Date updtdDate;
    int version;

    static constraints = {
        distrctName (nullable: false, blank: false, maxSize: 200, unique: true )
        latVal(nullable: false, blank: false)
        lngVal(nullable: false, blank: false)
        state (nullable: false, blank: false)
        delFlg (nullable: false, blank: false, maxSize: 1, inList: ['Y','N'])
        statsFlg (nullable: false, blank: false, maxSize: 1, inList: ['A','I'])
        cretnBy (nullable: false, blank: false)
        cretnDate (nullable: false, blank: false)
        updtdBy (nullable: false, blank: false)
        updtdDate (nullable: true, blank: false)
    }

    static mapping = {

        id column: 'distrct_id';
    }

}
