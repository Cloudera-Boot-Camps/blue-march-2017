## Test
To test with a single message:
```
[ec2-user@ip-172-31-12-6 ~]$ cat test_msg.txt 
4baaffa1-bd3c-4fba-9be0-5734a0ea0196,2,1,71,1490899038154,0.25294615647429053,0.8163441187270423,0.4671195585443785
[ec2-user@ip-172-31-12-6 ~]$ cat test_msg.txt  | netcat localhost 9999
```





## lssues
- socket stream stops
  - Description: The generate script stops sending messages to the sockets. Occurs when the channel is memory.
  - Cause: not 100% confirmed but cause appears to be the socket getting overload. 
  - Solution: Implement kafka as the channel, see flume conf files for setting.

- Error message seen
```
Caused by: org.apache.flume.ChannelFullException: Space for commit to queue couldn't be acquired. Sinks are likely not keeping up with sources, or the buffer size is too tight
```
- Solution: set channel capacity in flume configuration
  - tier1.channels.channel1.capacity = 100

