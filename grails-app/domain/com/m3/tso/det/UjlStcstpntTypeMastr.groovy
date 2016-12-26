package com.m3.tso.det

class UjlStcstpntTypeMastr {

    String stcstpntTypeName;

    String delFlg = 'N';
    String statsFlg = 'A';
    int cretnBy;
    Date cretnDate;
    int updtdBy;
    Date updtdDate;
    int version;

    static constraints = {

        stcstpntTypeName (nullable: false, blank: false, maxSize: 200, unique: true )

        delFlg (nullable: false, blank: false, maxSize: 1, inList: ['Y','N'])
        statsFlg (nullable: false, blank: false, maxSize: 1, inList: ['A','I'])
        cretnBy (nullable: false, blank: false)
        cretnDate (nullable: false, blank: false)
        updtdBy (nullable: false, blank: false)
        updtdDate (nullable: true, blank: false)
    }

    static mapping = {
        id column: 'stcstpnt_type_id';
    }
}
