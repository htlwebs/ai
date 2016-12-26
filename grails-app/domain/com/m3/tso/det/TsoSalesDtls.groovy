package com.m3.tso.det

class TsoSalesDtls {

    String zoneCode;
    String stateCode;
    String custmrCode;

    TsoBrndMastr brnd
    TsoCatgryMastr catgry
    int salesYear
    int salesQutr

    BigInteger salesVol

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

        zoneCode(nullable: false, blank: false,maxSize: 50);
        stateCode(nullable: false, blank: false,maxSize: 50);
        custmrCode(nullable: false, blank: false,maxSize: 50);

        brnd(nullable: false, blank: false);
        catgry(nullable: false, blank: false);
        salesYear(nullable: false, blank: false);
        salesQutr(nullable: false, blank: false);

        salesVol(nullable: false, blank: false);

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
            id column: "sales_dtls_id"
            cretnBy column: "cretn_by"
            updtdBy column: "updtd_by"
            deltdBy column: "deltd_by"

        }
    }
}
