# Samza SQL tools
Tool kit that helps you to play with Samza SQL. 


## Building

Clone apache samza repository and build it by running the following comamnds

```shell

# clone samza

cd ~
git clone https://github.com/apache/samza.git


# Build samza

cd samza
./gradlew build -PrunIntegrationTests


# Publish the artifacts to local maven repository

 ./gradlew publishToMavenLocal
```


Clone the samza-sql-tools repository and build it

```shell

# clone samza-sql-tools

cd ~
git clone https://github.com/srinipunuru/samza-sql-tools.git


# build samza-sql-tools.

cd samza-sql-tools
./gradlew clean releaseTarGz

```

Samza-sql-tools build creates a tar-ball under ~/samza-sql-tools/samza-sql-demo/build/distributions/. Untar it by running the below commands.

``` shell
cd  ~/samza-sql-tools/samza-sql-demo/build/distributions/
tar -xvzf *.tgz
cd samza-sql-demo-<version>
```

## Using Samza SQL tools


### Generate kafka events


Generate kafka events tool is used to insert avro serialized events into kafka topics. Right now it can insert two types of events [PageViewEvent](https://github.com/srinipunuru/samza-sql-tools/blob/master/samza-sql-demo/src/main/java/com/linkedin/samza/tools/schemas/PageViewEvent.avsc) and [ProfileChangeEvent](https://github.com/srinipunuru/samza-sql-tools/blob/master/samza-sql-demo/src/main/java/com/linkedin/samza/tools/schemas/ProfileChangeEvent.avsc)

Before you can generate kafka events, Please follow instructions [here](http://kafka.apache.org/quickstart) to start the zookeeper and kafka server on your local machine.

You can follow below instructions on how to use Generate kafka events tool.


``` shell

# Usage of the tool

./scripts/generate-kafka-events.sh
usage: Error: Missing required options: t, e
              generate-kafka-events.sh
 -b,--broker <BROKER>               Kafka broker endpoint Default (localhost:9092).
 -n,--numEvents <NUM_EVENTS>        Number of events to be produced, 
                                    Default - Produces events continuously every second.
 -p,--partitions <NUM_PARTITIONS>   Number of partitions in the topic,
                                    Default (4).
 -t,--topic <TOPIC_NAME>            Name of the topic to write events to.
 -e,--eventtype <EVENT_TYPE>        Type of the event values can be (PageView|ProfileChange). 


# Example command to generate 100 events of type PageViewEvent into topic named PageViewStream

 ./scripts/generate-kafka-events.sh -t PageViewStream -e PageView -n 100


# Example command to generate ProfileChange events continuously into topic named ProfileChangeStream

 ./scripts/generate-kafka-events.sh -t ProfileChangeStream -e ProfileChange 

```

### Samza SQL console tool

Once you generated the events into the kafka topic. Now you can use samza-sql-console tool to perform processing on the events published into the kafka topic.

There are two ways to use the tool -

1. You can either pass the sql statement directly to the tool. 
2. You can write the sql statement(s) into a file and pass the sql file as an argument to the tool.

Second option allows you to execute multiple sql statements, whereas the first one lets you execute one at a time.

Samza SQL needs all the events in the topic to be uniform schema. And it also needs access to the schema corresponding to the events in a topic. Typically in an organization, there is a deployment of schema registry which maps topics to schemas. 

In the absence of schema registry, Samza SQL console tool uses the convention to identify the schemas associated with the topic. If the topic name has string "page" it assumes the topic has PageViewEvents else ProfileChangeEvents. 

Would be useful to briefly describe the mechanism by which Samza SQL leverages a schema registry. e.g. Does it rely on the Samza System & Serde abstractions or does it interact more directly with the registry?

```shell

# Usage of the tool

 ./scripts/samza-sql-console.sh
usage: Error: One of the (f or s) options needs to be set
              samza-sql-console.sh
 -f,--file <SQL_FILE>   Path to the SQL file to execute.
 -s,--sql <SQL_STMT>    SQL statement to execute.

# Example command to filter out all the users who have moved to LinkedIn

./scripts/samza-sql-console.sh --sql "Insert into log.consoleOutput select Name,OldCompany from ProfileChangeStream where NewCompany = 'LINKEDIN'"

```

Here would be a good place to reference any documented samples of other queries so users can get a feel for what they can do with Samza SQL.

### Event Hub Consumer

This feels out of place. It comes out of nowhere and the reader may wonder why they care about Event Hubs in the context of Samza SQL. It's also unclear how it works. Does it emit the EH events to a Kafka topic that Samza SQL can then consume?

This tool lets you consume events from the Microsoft EventHubs stream. This tool assumes that the payload of the events is String. 

```shell

# Usage of the tool

./scripts/eh-consumer.sh
usage: Error: Missing required options: e, n, k, t
              eh-console-consumer.sh
 -e,--ehname <EVENTHUB_NAME>           Name of the event hub.
 -k,--key <KEY_NAME>                   Name of the key.
 -n,--namespace <EVENTHUB_NAMESPACE>   Namespace of the event hub.
 -t,--token <TOKEN>                    Token corresponding to the key.

 # Example command to consume from an event hub
 ./scripts/eh-consumer.sh -e OutputStream -n srinieh1 -k <SasKeyName> -t <SasToken>

```


## Developing using Idea

You can use intellij for developing. You can build the intellij project files by running

```shell
./gradlew idea
```

Once the intellij project files (*.ipr) are created, You can open them using Intellij and Start developing.

### Contributing and submitting patches

Contributions are accepted in the form of pull requests, please use [this](https://help.github.com/articles/using-pull-requests/) on how to submit the pull request. 

Before you submit the pull request, ensure that your changes in your fork builds and tests run with the latest changes from upstream. To sync the changes from the main repository into your fork you can follow the instructions [here](https://help.github.com/articles/syncing-a-fork/)

