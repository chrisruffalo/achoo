# Achoo                                
--------------                         

## Purpose

Achoo is a REST based messaging system designed around Akka and intented to be flexible and, above all, easy to  use.  It is designed to work with the simplest of technologies 
without requiring expensive clients, multiple threads, or esoteric protocols.  What you put in is what you get out the other end.                                               

## Technologies

* Akka 2 - distributed message passing, actor based multi-threaded communication
* Logback and SLF4J - logging, plain and simple
* Jetty 8 - fast and full-featured web container
* JBoss Weld - CDI and other magic
* RESTEasy - simple rest services
* JCommander - command line parsing
* Typesafe Config - easy to use configuration library
* Guava - the thousand little things we don't want to write ourselves

## Road Map

* 1.0 - Churchill
  * Broadcast Topic
  * Round Robin Queue
  * Non-durable (in memory) message passing
  * Ad-Hoc subscribing and unsubscribing
  * Message Recieve Types
      * On Demand with in memory storage
      * HTTP Callback
      * TCP Callback
      * TCP Multicast Callback

* 1.1 - Scipio
  * Configurable, Durable in-flight characteristics (messages no longer lost on shutdown)
  * Configurable Queue Types (adding SmallestMailbox and Scatter-Gather)
  * Durable (JBDC/Hibernate) On Demand storage
  * Durable Subscribers

* 1.2 - Turing
  * More flexible topic-queue system
      * subscribe to all
      * subscribe by pattern
      * subscribe by type (queue/topic)
  * System management messages
      * tell users when new queue or topic is available
      * tell users when a kill order has come in

* 1.3 - Cinderella
  * System statistics (throughput, etc)
  * Simple GUI

* 1.4 - Sterling
  * Users shall be able to configure which endpoints (management, subscribe, send) go on a particular interface
  * SSL support for Jetty
  * HTTPS callback support
