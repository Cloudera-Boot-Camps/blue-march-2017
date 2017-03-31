package com.cloudera.fce.bootcamp;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;

public class MeasurementKafkaGen {

  @SuppressWarnings("resource")
  public static void main(String[] args) throws Exception {

    Properties props = new Properties();
    props.put("bootstrap.servers", "ip-172-31-9-124.us-west-2.compute.internal:9092,ip-172-31-5-78.us-west-2.compute.internal:9092,ip-172-31-4-187.us-west-2.compute.internal:9092,ip-172-31-12-6.us-west-2.compute.internal:9092");
    props.put("acks", "all");
    props.put("retries", 0);
    props.put("batch.size", 16384);
    props.put("linger.ms", 1);
    props.put("buffer.memory", 33554432);
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

    Producer<String, String> producer = new KafkaProducer<String, String>(props);

    Long i = 0L;

    while (true) {
      Random random = new Random();

      String measurementID = UUID.randomUUID().toString();
      int detectorID = random.nextInt(8) + 1;
      int galaxyID = random.nextInt(128) + 1;
      int astrophysicistID = random.nextInt(106) + 1;
      long measurementTime = System.currentTimeMillis();
      double amplitude1 = random.nextDouble();
      double amplitude2 = random.nextDouble();
      double amplitude3 = random.nextDouble();

      String delimiter = ",";
      String measurement = measurementID + delimiter + detectorID + delimiter + galaxyID +
          delimiter + astrophysicistID + delimiter + measurementTime + delimiter +
          amplitude1 + delimiter + amplitude2 + delimiter + amplitude3;

      producer.send(new ProducerRecord<String, String>("measurements2", measurementID, measurement));

      if (++i % 1000 == 0) {
        System.out.println("Sent ttl of " + i);
      }
    }

  }

}
