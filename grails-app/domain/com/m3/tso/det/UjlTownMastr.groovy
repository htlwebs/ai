package com.m3.tso.det

class UjlTownMastr {

    String townName;

    String townCatgry;
    String townStats;
    int townType;

    double townPopltn;

    double latVal = 0.00;
    double lngVal = 0.00;

    UjlStateMastr state;
    UjlDistrctMastr distrct;
    String pinCode;

    String delFlg = 'N';
    String statsFlg = 'A';
    int cretnBy;
    Date cretnDate;
    int updtdBy;
    Date updtdDate;
    int version;

    static constraints = {
        townName (nullable: false, blank: false, maxSize: 200 )
        latVal(nullable: false, blank: false)
        lngVal(nullable: false, blank: false)
        pinCode(nullable: true, blank: true)

        state (nullable: false, blank: false)
        distrct (nullable: false, blank: false)

        townCatgry (nullable: false, blank: false, maxSize: 1, inList: ['T','V'])
        townStats (nullable: false, blank: false, maxSize: 20)
        townPopltn (nullable: false, blank: false)


        delFlg (nullable: false, blank: false, maxSize: 1, inList: ['Y','N'])
        statsFlg (nullable: false, blank: false, maxSize: 1, inList: ['A','I'])
        cretnBy (nullable: false, blank: false)
        cretnDate (nullable: false, blank: false)
        updtdBy (nullable: false, blank: false)
        updtdDate (nullable: true, blank: false)
    }

    static mapping = {

        id column: 'town_id';
    }
}
