package com.m3.tso.det

class TsoTargtDtls {

    TsoUsrMastr usr
    String usrCode;

    TsoSragrpMastr sraGrp
    TsoBrndMastr brnd
    TsoCatgryMastr catgry
    int targtYear
    int targtQutr

    BigInteger targtVol
    BigInteger achvmntVol

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

        usrCode(nullable: false, blank: false,maxSize: 50);
        sraGrp(nullable: false, blank: false);
        brnd(nullable: false, blank: false);
        catgry(nullable: false, blank: false);
        targtYear(nullable: false, blank: false);
        targtQutr(nullable: false, blank: false);

        targtVol(nullable: false, blank: false);
        achvmntVol(nullable: false, blank: false);

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
            id column: "targt_id"
            cretnBy column: "cretn_by"
            updtdBy column: "updtd_by"
            deltdBy column: "deltd_by"

        }
    }
}
