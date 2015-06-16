#!/bin/sh

asadmin start-domain

asadmin create-jdbc-connection-pool --datasourceclassname com.mysql.jdbc.jdbc2.optional.MysqlDataSource --restype javax.sql.DataSource --property "User=$DTX_DB_USER_NAME:Password=$DTX_DB_SECRET:URL=jdbc\\:mysql\\://$DTX_DB_SERVER_NAME\\:3306/$DTX_DB_NAME" DastraxProPool
asadmin create-jdbc-resource --connectionpoolid DastraxProPool jdbc/DastraxPro

asadmin deploy --contextroot / --name Dastrax /*.war
asadmin stop-domain
asadmin start-domain --verbose