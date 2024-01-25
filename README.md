## [REST API](http://localhost:8080/doc)

## Description:
JiraRush is a web application which has a similar concept to Jira and Trello.
This project differs from other JavaRush projects in that it did not have to be 
written from scratch. The main task was to complete the existing project (not 
completely, but only "team" tasks), completing the maximum number of tasks.

## Team tasks
#### Completed:
1. Understand the project structure (onboarding).
2. Delete social networks: vk, yandex.
3. Place sensitive information in a separate property file. The values of these 
properties must be read from the machine’s environment variables when the server 
starts.
4. Rework the tests so that during the tests an in-memory database (H2) is used, 
and not PostgreSQL.
5. Write tests for all public methods of the ProfileRestController controller.
6. Add new functionality: adding tags to a task (rest, backend).  
   *additionally from myself: getting a list of tasks by tag name*
7. Add the ability to subscribe to tasks that are not assigned to the current user (backend, REST).
8. Add automatic calculation of the time the task was in work and testing 
(backend, REST).  
*on my own: implemented by adding a new TaskTime entity, which stores 
information about the development and testing time of the task. When the task 
status changes to ready or done, StatusListeners is triggered, through which 
the corresponding TaskTimeService methods are called to calculate and write 
values. Also, when changing the task status back to ready or done, the previous 
values are taken into account*
9. Write a Dockerfile for the main server
10. Write a docker-compose file to launch the server container along with the 
database and nginx.
11. Add localization in at least two languages for email templates and the 
index.html start page.
12. Implement backlog - a complete list of tasks (with paging) that must be 
completed and do not yet belong to any sprint. (back + front).

#### Not done:
13. Rework the “friend or foe” recognition mechanism between front and back from 
JSESSIONID to JWT.  
*reason: it was not possible to add sending a token from the client via header, 
and also to configure oaut2 to work with JWT. Also, upon successful token 
verification, a 403 error was returned, i.e. after authorization the user 
lost access to the site*

*p.s. another interesting bug: if there is “view” in the path, for example: 
localhost:8080/view/login, then it is impossible to switch localization, 
request parameters are ignored (cookies are not updated)*

## Launch
1. download and unzip [jira-rush-vika-kov.zip](https://drive.google.com/file/d/19x5-kHp0wz2L7vfSZ0rkU6dgUlgYjCkS/view?usp=drive_link)
2. run Docker
3. open the command line and enter:  
```$ docker-compose up --build -d```
4. after launch open the browser and go to the address ```localhost:80```  
**_(!) it takes some time to connect all services. If you get a 502 Bad Gateway 
error, refresh the page after 30-40 seconds_**

## Technologies
* Java 17, Maven, Docker
* Spring, PostgreSQL, H2 in-memory DB
* Servlets, log4j, mapstruct, junit, mockito, lombok
* HTML, CSS, JavaScript, Bootstrap
