# spring-ha-consul-zuul
This is a prototype of load balancing two spring boot services runing on same server, with two ports
Key players:
- consul
- fast-startup
- zuul-service

1. **Start consul**
Download consul https://www.consul.io/downloads.html
In consul-server.json Change bind_addr and client_addr to <your-ip>
For Windows, run following command in a cmd window
...
consul agent --config-file consul-server.json
...
Visit http://<your-ip>:8500/ui/dc1/kv
Create a key config/faststartup/data
Value 
...
greeting: hello
name: world
...

2. **Start fast-startup with port 8050**
In fast-startup/build.gradle, change bootRun args --consul.host=<your-ip>, same with consul bind_addr and client_addr
Open another cmd, navigate to fast-startup, run the following
...
gradlew bootRun
...

3. **Start fast-startup with port 8051**
In fast-startup/build.gradle, change bootRun args --port=8051
Open another cmd, navigate to fast-startup, run the following
...
gradlew bootRun
...

4. **Start zuul-service**
In zuul-service/build.gradle, change bootRun args --consul.host=<your-ip>, same with consul bind_addr and client_addr
Open another cmd, navigate to fast-startup, run the following
...
gradlew bootRun
...

consul is running on default port 8500
fast-startup-8050 and fast-startup-8051 will read configuration from consul's key config/faststartup/data
fast-startup expose /api/greeting
zuul-service is running on 8040
zuul-service picks up detect fast-startup api

visit http://ip:8050/api/greeting, confirming result has 8050
visit http://ip:8051/api/greeting, confirming result has 8051

visit http://ip:8400/faststartup/api/greeting many times, confirming result alternate between 8050 and 8051
