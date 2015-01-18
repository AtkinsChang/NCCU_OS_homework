# NCCU_OS_homework
## Build
### Requirement
* Java 8
* Maven 3

### Command
```
mvn clean package
```

## Simple
Simple version contain one porducer one consumer. 
* Producer read user input and transform to bit array (1 char => 16 bits), then put random bits into buffer, sleep random time and go on.
* Consumer take random bit from buffer, sleep random time and go on.
The correctness of string composed from bit prove the correctness of multual exclusive;

## Standalone
Standalone version can config multiple producers and consumers for a queue by JMX.

## Web
Web version require full Java EE environment. The web ui provide the configuration of producer and consumer and display the state of each component.
