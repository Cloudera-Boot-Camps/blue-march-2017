# Sqoop Documentation


## Import Database in shell
```
HADOOP_USER_NAME=hive /usr/bin/sqoop -import --connect jdbc:oracle:thin:@bootcamp-march2017.cghfmcr8k3ia.us-west-2.rds.amazonaws.com:15210:gravity --username gravity -password gravity  --table ${oracletable}  --target-dir /user/admin/staging/${stagingdir} --fields-terminated-by ^ --direct -m ${mappers} -z
```
