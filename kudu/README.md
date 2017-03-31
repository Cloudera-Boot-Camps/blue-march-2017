## Create table in Impala shell

```
[ip-172-31-9-124.us-west-2.compute.internal:21000] > CREATE TABLE presentation.DETECTORS_KUDU
PRIMARY KEY (detector_id)
PARTITION BY HASH(detector_id) PARTITIONS 8
STORED AS KUDU                                                                    
TBLPROPERTIES("kudu.master_addresses"="ip-172-31-12-6.us-west-2.compute.internal")
AS SELECT detector_id, detector_name FROM presentation.detectors;
Query: create TABLE presentation.DETECTORS_KUDU
PRIMARY KEY (detector_id)
PARTITION BY HASH(detector_id) PARTITIONS 8
STORED AS KUDU
TBLPROPERTIES("kudu.master_addresses"="ip-172-31-12-6.us-west-2.compute.internal")
AS SELECT detector_id, detector_name FROM presentation.detectors
+-------------------+
| summary           |
+-------------------+
| Inserted 8 row(s) |
+-------------------+
```

## Select table

```
[ip-172-31-9-124.us-west-2.compute.internal:21000] > select * from presentation.DETECTORS_KUDU;
Query: select * from presentation.DETECTORS_KUDU
Query submitted at: 2017-03-30 21:23:48 (Coordinator: http://ip-172-31-9-124.us-west-2.compute.internal:25000)
Query progress can be monitored at: http://ip-172-31-9-124.us-west-2.compute.internal:25000/query_plan?query_id=4848a261f8fe4347:521dc82b00000000
+-------------+------------------+
| detector_id | detector_name    |
+-------------+------------------+
| 1           | LIGO Hanford     |
| 2           | LIGO Livingstone |
| 6           | KAGRA            |
| 5           | Virgo            |
| 7           | GEO600           |
| 8           | AURIGA           |
| 4           | TAMA 300         |
| 3           | MiniGRAIL        |
+-------------+------------------+
Fetched 8 row(s) in 4.82s
```

## Update record

```
[ip-172-31-9-124.us-west-2.compute.internal:21000] > update presentation.DETECTORS_KUDU set detector_name='Callum Hogg' where detector_id=1;
Query: update presentation.DETECTORS_KUDU set detector_name='Callum Hogg' where detector_id=1
Query submitted at: 2017-03-30 21:28:49 (Coordinator: http://ip-172-31-9-124.us-west-2.compute.internal:25000)
Query progress can be monitored at: http://ip-172-31-9-124.us-west-2.compute.internal:25000/query_plan?query_id=834ad4b4fb5b8592:3451768900000000
Modified 1 row(s), 0 row error(s) in 0.13s
```

## Select the modified table
```
[ip-172-31-9-124.us-west-2.compute.internal:21000] > select * from presentation.DETECTORS_KUDU;
Query: select * from presentation.DETECTORS_KUDU
Query submitted at: 2017-03-30 21:25:42 (Coordinator: http://ip-172-31-9-124.us-west-2.compute.internal:25000)
Query progress can be monitored at: http://ip-172-31-9-124.us-west-2.compute.internal:25000/query_plan?query_id=ee45b60ed9b8a57d:a0060f8800000000
+-------------+------------------+
| detector_id | detector_name    |
+-------------+------------------+
| 1           | Callum Hogg      | <-- CHANGED
| 2           | LIGO Livingstone |
| 6           | KAGRA            |
| 5           | Virgo            |
| 7           | GEO600           |
| 4           | TAMA 300         |
| 3           | MiniGRAIL        |
| 8           | AURIGA           |
+-------------+------------------+
Fetched 8 row(s) in 0.14s
```
