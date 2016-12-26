environments {
    development {
        dataSource {
            dbCreate = "none"
            url = "jdbc:mysql://localhost/aidb"
            driverClassName = "com.mysql.jdbc.Driver"
            username = "root"
            password = "pass"
            dialect = org.hibernate.dialect.MySQL5InnoDBDialect
            properties {
                maxActive = 50
                maxIdle = 25
                minIdle = 1
                initialSize = 1
                minEvictableIdleTimeMillis = 60000
                timeBetweenEvictionRunsMillis = 60000
                numTestsPerEvictionRun = 3
                maxWait = 10000

                testOnBorrow = true
                testWhileIdle = true
                testOnReturn = true

                validationQuery = "select now()"
            }
        }
    }
    test {
        dataSource {
            dbCreate = "update"
            url = "jdbc:mysql://localhost/aidb"
            driverClassName = "com.mysql.jdbc.Driver"
            username = "root"
            password = "ikyaSql"
            dialect = org.hibernate.dialect.MySQL5InnoDBDialect
        }
    }
    production {
        dataSource {
            dbCreate = "none"
            url = "jdbc:mysql://localhost:3306/aidb"
            driverClassName = "com.mysql.jdbc.Driver"
            username = "root"
            password = "pass"
            dialect = org.hibernate.dialect.MySQL5InnoDBDialect
            properties {
                maxActive = 50
                maxIdle = 25
                minIdle = 1
                initialSize = 1
                minEvictableIdleTimeMillis = 60000
                timeBetweenEvictionRunsMillis = 60000
                numTestsPerEvictionRun = 3
                maxWait = 10000

                testOnBorrow = true
                testWhileIdle = true
                testOnReturn = true

                validationQuery = "select now()"
            }
        }
    }
}
