package com.m3.tso.det

class TsoCatgryMastr {

    String catgryName;
    String catgryCd;

    //Common Fields
    String delFlg = "N"   // delFlg should be N/Y. if deleted state, delFlg = 'Y'
    String statsFlg = "A" // statsFlg should be A/I. A= Active I= Inactive
    TsoUsrMastr cretnBy
    Date cretnDate
    TsoUsrMastr updtdBy
    Date updtdDate
    TsoUsrMastr deltdBy
    Date deltdDate
    int version

    static constraints = {

        catgryName(nullable: false, blank: false, unique: true, maxSize: 500);
        catgryCd(nullable: false, blank: false, unique: true, maxSize: 50);

        //Common Fields
        delFlg(nullable: false, blank: false, maxSize: 1, inList: ['N', 'Y'])
        statsFlg(nullable: false, blank: false, maxSize: 1, inList: ['A', 'I'])
        cretnBy(nullable: false, blank: false)
        cretnDate(nullable: false, blank: false)
        updtdBy(nullable: true)
        updtdDate(nullable: true)
        deltdBy(nullable: true)
        deltdDate(nullable: true)
        version(nullable: false, blank: false)

    }

    static mapping = {
        columns {
            id column: "catgry_id"
            cretnBy column: "cretn_by"
            updtdBy column: "updtd_by"
            deltdBy column: "deltd_by"

        }
    }
}
