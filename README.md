[![Application Tests](https://github.com/BranislavBeno/Ronja-CRM-Desktop-Client/actions/workflows/tests.yml/badge.svg)](https://github.com/BranislavBeno/Ronja-CRM-Desktop-Client/actions/workflows/tests.yml)  
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=com.ronja.crm.ronjaclient%3Aronja-parent&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=com.ronja.crm.ronjaclient%3Aronja-parent)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=com.ronja.crm.ronjaclient%3Aronja-parent&metric=coverage)](https://sonarcloud.io/dashboard?id=com.ronja.crm.ronjaclient%3Aronja-parent)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=com.ronja.crm.ronjaclient%3Aronja-parent&metric=ncloc)](https://sonarcloud.io/dashboard?id=com.ronja.crm.ronjaclient%3Aronja-parent)  
[![](https://img.shields.io/badge/Java-19-blue)](/pom.xml)
[![](https://img.shields.io/badge/JavaFX-18.0.2-blue)](/pom.xml)
[![](https://img.shields.io/badge/Spring%20Boot-3.0.1-blue)](/pom.xml)
[![](https://img.shields.io/badge/Testcontainers-1.17.6-blue)](/pom.xml)
[![](https://img.shields.io/badge/Maven-3.8.6-blue)](https://img.shields.io/badge/maven-v3.8.6-blue)
[![](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)

## Ronja CRM client
Application is desktop client for communication with respective CRM server, implemented in JavaFX and Spring Boot.    
The application is preconfigured to connect `REST API Ronja Server`, which provides communication with the database.  
In case you would like to run this client application, it's necessary first start `Ronja Server` via [docker-compose](/docker-compose.yml).

### Installation
Preferred way of installation is to build and run the application as a fat jar on any hosting OS with Java 19 installed.  
When a hosting system doesn't provide JRE in version 19, it's recommended to bundle the respective fat jar with its own JRE
of correct Java version.  
It's also recommended to bundle fat jar with application properties file. This file is intended, among other things,
to set GUI language. User can choose between slovak and english language.
User can also change url for connection with `REST API Ronja Server`.

### Usage
Application start takes few seconds, due to prerequisite connection establishing to `REST API Ronja Server`.
After successful start, application shows following tabs:

#### Dashboard
Provides overview about:
- scheduled meetings with customer representatives
- actual metal prices for copper, aluminium and lead according to London metal exchange
- metal prices progress time line

![](docs/images/ronja-client-dash-board.png)

#### Customers
Allows user to handle customers records:
- show customers list
- provide advanced filtering over customers list
- add, delete or change particular customer
- handle representatives related to respective customer


![](docs/images/ronja-client-customers.png)

#### Representatives
Allows user to handle representatives records:
- show representatives list
- provide advanced filtering over representatives list
- add, delete or change particular representative


![](docs/images/ronja-client-representatives.png)
