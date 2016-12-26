package com.m3.tso.det

class TsoUsrMastr {

    String usrEmpCd;
    String usrCd;
    String usrName;
    String usrEmail;
    String usrPaswrd;
    String usrMobleNo;
    String loginType;

    TsoUsrTypeMastr usrType;
    TsoUsrMastr reprtngUsr;

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

        usrEmpCd(nullable: false, blank: false, unique: true, maxSize: 50);
        usrCd(nullable: false, blank: false, unique: true, maxSize: 200);
        usrName(nullable: false, blank: false, maxSize: 200);
        usrType(nullable: false, blank: false);
        usrEmail(nullable: false, blank: false, unique: true, maxSize: 250, email: true);
        usrPaswrd(nullable: false, blank: false, maxSize: 30);
        loginType(nullable: false, blank: false, maxSize: 1, inList: ['L', 'N'])
        usrMobleNo(nullable: false, blank: false, maxSize: 20);
        reprtngUsr(nullable: true, blank: false);

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
            id column: "usr_id"
            cretnBy column: "cretn_by"
            updtdBy column: "updtd_by"
            deltdBy column: "deltd_by"

        }
    }
}
