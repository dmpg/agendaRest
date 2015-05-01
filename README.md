# agendaRest
This is just a sandbox to play with the Spring Boot...
The idea is to demo how to implement RESTful services using Full Spring Stack without XML configurations

# Install
mvn clean install

#Run
mvn spring-boot:run

# Try rest-shell
http://localhost:8080:> follow api
http://localhost:8080/api:> get agendas
> GET http://localhost:8080/api/agendas

http://localhost:8080/api:> get agendas/1
> GET http://localhost:8080/api/agendas/1

http://localhost:8080/api:> get agendas/1/contacts
> GET http://localhost:8080/api/agendas/1/contacts

http://localhost:8080/api:> get agendas/1/contacts/1
> GET http://localhost:8080/api/agendas/1/contacts/1

# Contribute!
