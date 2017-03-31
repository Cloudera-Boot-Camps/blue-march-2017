# Apache Envelope test
Source: [Envelope project](https://github.com/cloudera-labs/envelope/blob/master/README.md)

Run a spark streaming job to read from a kafka topic all the measures and save only the exceptional ones to a Kudu table

## Compile Apache Envelope
First change the dependencies versions to match the runtime in the pom file. We did as follows:
```
<properties>
    <spark.version>1.6.0-cdh5.10.0</spark.version>
    <kafka.version>0.9.0-kafka-2.0.0</kafka.version>
    <kudu.version>1.2.0</kudu.version>
    <junit.version>4.10</junit.version>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
</properties>
```
then:
```
mvn package
```
It created the jar file `envelope-0.1.0.jar`

## Create a KUDU table
In impala:
```
CREATE TABLE default.anomalies_kudu
(
MEASUREID STRING,
DETECTORID INT,
GALAXYID INT,
ASTROPHYSICISTID INT,
MEASUREMENTTYPE DOUBLE,
AMPLITUDE1 FLOAT,
AMPLITUDE2 FLOAT,
AMPLITUDE3 FLOAT,
PRIMARY KEY (MEASUREID)
)
PARTITION BY HASH(MEASUREID) PARTITIONS 8
STORED AS KUDU                                                                    
TBLPROPERTIES("kudu.master_addresses"="ip-172-31-12-6.us-west-2.compute.internal");
```

## Envelope properties file
Note that the Kudu table is `"impala::<database>.<tablename>"`

```
application.name=BlueApplicarionEnvelope
application.batch.milliseconds=10000
application.executors=2
application.executor.cores=1
application.executor.memory=1G

source=kafka
source.kafka.brokers=ip-172-31-12-6.us-west-2.compute.internal:9092
source.kafka.topics=kudutest
source.kafka.encoding=string

source.repartition=true
source.repartition.partitions=2

translator=delimited
translator.delimited.delimiter=,
translator.delimited.field.names=measureid,detectorid,galaxyid,astrophysicistid,measurementtype,amplitude1,amplitude2,amplitude3
translator.delimited.field.types=string,int,int,int,double,float,float,float

flows=anomalies

flow.anomalies.deriver=sql
flow.anomalies.deriver.query.literal=SELECT measureid,detectorid,galaxyid,astrophysicistid,measurementtype,amplitude1,amplitude2,amplitude3 FROM stream WHERE amplitude1>0.995 AND amplitude2<0.005 AND amplitude3>0.995
flow.anomalies.planner=append
flow.anomalies.storage=kudu
flow.anomalies.storage.connection=ip-172-31-12-6.us-west-2.compute.internal:7051
flow.anomalies.storage.table.name=impala::default.anomalies_kudu
flow.anomalies.storage.table.columns.key=MEASURE_ID
flow.anomalies.storage.table.columns.last.updated=lastupdated
```

## Run spark streaming job
```
spark-submit envelope-0.1.0.jar ./envelope.properties
```

