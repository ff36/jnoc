#!/bin/sh

asadmin start-domain

asadmin create-jdbc-connection-pool --datasourceclassname com.mysql.jdbc.jdbc2.optional.MysqlDataSource --restype javax.sql.DataSource --property "User=$JNOC_DB_USER_NAME:Password=$JNOC_DB_SECRET:URL=jdbc\\:mysql\\://$JNOC_DB_SERVER_NAME\\:3306/$JNOC_DB_NAME" JnocProPool
asadmin create-jdbc-resource --connectionpoolid JnocProPool jdbc/JnocPro

asadmin deploy --contextroot / --name Jnoc /*.war
asadmin stop-domain
asadmin start-domain --verbose