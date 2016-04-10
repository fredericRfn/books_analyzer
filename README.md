UNIZAR - Tecnologías y Modelos para el Desarrollo de Aplicaciones Distribuidas
==============================================================================

Practice session 0: Making a spring RESTful application based on twitter API
----------------------------------------------------------------------------
 
### I - Usage and principles -
 
This little application allows the user to gather tweets which contain a certain keyword.
Here is the link of the app deployed in Heroku.

https://lit-shore-22785.herokuapp.com/

Please note that the secrets necessary to use the twitter API were removed. Therefore make sure to get your own and to fill the application.properties file accordingly if you build the application from this repo.

The server is composed of 2 controllers. The first sends the html and the js scripts to the client, and the other one is responsible for forwarding JSONs to the client (responding to AJAX requests triggered when the "buscar" button is clicked).

This application is a Spring-Boot application, wrapped by gradle.

### II - Steps to make this app RestFul

This project is a fork of another one, which implements the traditional MVC pattern. We decided to spread the processing work between the server and the client by letting the client format the raw JSONs returned by AJAX requests.

  1. Editing the SearchController in order to return only JSONs when url/search?q=keyword is called.
  2. Creating another controller, a standard one, to provide the client the JS scripts and the basic HTML it needs to display the application.
  3. Designing a Javascript function, triggered with onclick, which is responsible for doing an AJAX request, getting the JSON, applying it a Mustache template, and displaying the resulting HTML.

A few things were a bit tricky however.
 
  1. **Thymeleaf** IS NOT SUPPORTED BY HEROKU, or partially in certain conditions (using some Maven configuration files and building specific folder trees)
Getting rid of thymeleaf was not that easy. The assets were all moved in a public directory, and all the directive including th were erased, and src attributes changed accordingly.
  2. **mustache.js** must be included in the head section of the html part. It can be found in Github.
  3. Heroku won't allow the application to request **insecure URLs**, therefore it is necessary to ensure that **every single request made by an app deployed in Heroku will be served over the https protocol**.



