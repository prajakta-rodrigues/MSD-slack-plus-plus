# Team 207 Managing Software Development Spring 2019 - Northeastern University

## Overview
This repository contains the project we developed for our course CS5500 at Northeastern University. It is a chat server that supports the following unique functionalities along with the basic chat server functions:
* Adding friends
* Creating groups
* Real-time notifications
* Broadcasting/Recalling/translate messages
* Do not disturb mode
* Special govt users with wiretapping feature

## Team Members:
* Omar Tuffaha* (omartuffaha)\
* Prajakta Rodrigues* (prajaktarods)\
* Sean Ylescupidez* (seanyles)\
* Venkatesh koka* (venkateshkoka)

## Requirements:
* Java 8 or plus
* Maven 3.6
* MySQL Server
* Command Prompt/ any IDE

## Install:
1.	Clone the repository
2.	The steps for installing the database server and setting it up for the application are mentioned under team-207-SP19\Documents\database-setup.pdf
3.	In order to point the application server to the local database you need edit the config.properties file to your local database and  modify the credentials as per your database.
4.	Open the project in Command Prompt
5.	Go the following path:
team-207-SP19\Development\ChatServer
6.	Run the command: mvn clean install
7.	As this is a client server process, you need to start the server first using the following command:
java -jar target\Chatter-0.0.1-SNAPSHOT.jar
This gets the server started.
8.	Now you can connect to the server using client application. In order to start the client application, navigate to the following path in another terminal:
 team-207-SP19\Development\ChatServer
9.	In order to start the client, run the following command:
java -cp Chatter-0.0.1-SNAPSHOT.jar edu.northeastern.ccs.client.CommandLineMain localhost 4545 
10.	Once you enter a username and password as prompted, you will be registered in the system and can start using the application.

## Demos
Videos hosted on youtube relating to Slack++: \
*[System Demo]* (https://youtu.be/1w99Tk32ebc) \
*[System Setup]* (https://youtu.be/hISPZi5UynM) \
*[Final presentation]* (https://youtu.be/voQnCHp_aGA)
