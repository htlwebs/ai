package com.m3.tso.det

class UjlUsrMastr {

    String usrCd;
    String usrName;
    String usrEmail;
    String usrPaswrd;
    UjlRoleMastr role;
    String acesFlg;
    String brndAcesFlg;

    String delFlg = 'N';
    String statsFlg = 'A';
    int cretnBy;
    Date cretnDate;
    int updtdBy;
    Date updtdDate;
    int version;

    static constraints = {
        usrCd (nullable: false, blank: false, maxSize: 200, unique: true )
        usrName (nullable: false, blank: false, maxSize: 200)
        usrEmail (nullable: true, blank: false, email: true, maxSize: 200)
        usrPaswrd (nullable: false, blank: false, maxSize: 20 )

        acesFlg (nullable: false, blank: false, maxSize: 1, inList: ['A','S'])
        brndAcesFlg (nullable: false, blank: false, maxSize: 1, inList: ['A','S'])

        delFlg (nullable: false, blank: false, maxSize: 1, inList: ['Y','N'])
        statsFlg (nullable: false, blank: false, maxSize: 1, inList: ['A','I'])
        cretnBy (nullable: false, blank: false)
        cretnDate (nullable: false, blank: false)
        updtdBy (nullable: false, blank: false)
        updtdDate (nullable: true, blank: false)
    }

    static mapping = {

        id column: 'usr_id';
    }
}
