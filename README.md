# Expense Reimbursement System  

## Project Description  
The Expense Reimbursement System (ERS) will manage the process of reimbursing employees for expenses incurred while on company time. All employees in the company can login and submit requests for reimbursement and view their past tickets and pending requests. Finance managers can log in and view all reimbursement requests and past history for all employees in the company. Finance managers are authorized to approve and deny requests for expense reimbursement.  

## Technologies Used  
  - PostgreSQL - version 42.2.12  
  - Java - Version 8.9  
  - javax servlet - version 4.0.1  
  - Jackson - version 2.12.2  
  - Apache Logging - version 2.14.1  
  - Mockito - version 3.3.3  
  - Hibernate - version 5.4.28

## Features
  - Database connections map Java Objects using hibernate.  
  - Endpoints Exposed using Java Servlets in a RESTful manner.
  - Logging handled via Log4J. 
  - Custom Exceptions with clear and understandable Exception messages.  

To-do List:  
  - More complete Junit tests.  
  - More precises use of HTTP error response codes.  

## Getting started  
```shell
  git clone https://github.com/ECorpron/ERS_API_ChrisEli.git
  cd ./ERS_API_ChrisEli
  mvn compile
  mvn tomcat7:run
  ```
