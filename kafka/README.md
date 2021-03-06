# kafka Documentation

## Create a topic within Kafka
- Command
```
/usr/bin/kafka-topics --create --zookeeper <zookeeper hostname>:2181 --replication-factor 2 --partitions 4 --topic <topic name> 
```
- notes
  - Repilication-factor is limited by the maximum number of kafka brokers

## List topics
- Command
```
/usr/bin/kafka-topics --list --zookeeper <zookeeper hostname>:2181
```

## Test Topic

- Open a kafka consumers
```
/usr/bin/kafka-console-consumer --zookeeper <zookeeper hostname>:2181 --topic <topic>
```
- Create a producer, on a different console
```
/usr/bin/kafka-console-producer --broker-list <list of kafka brokers>:9092 --topic <topic>
```

- write text into the producers console, view the same text outputed in the consumers.
