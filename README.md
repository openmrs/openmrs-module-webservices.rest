<img src="https://talk.openmrs.org/uploads/default/original/2X/f/f1ec579b0398cb04c80a54c56da219b2440fe249.jpg" alt="OpenMRS"/>

# OpenMRS REST Web Services Module

> REST API for [OpenMRS](http://openmrs.org)

<a href="https://ci.openmrs.org/browse/RESTWS-RESTWS"><img src="https://omrs-shields.psbrandt.io/build/RESTWS/RESTWS" alt="Build"/></a>
<a href="https://modules.openmrs.org/#/show/153/webservices-rest"><img src="https://omrs-shields.psbrandt.io/version/153" alt="Version"/></a>
<a href="https://modules.openmrs.org/#/show/153/webservices-rest"><img src="https://omrs-shields.psbrandt.io/omrsversion/153" alt="OpenMRS Version"/></a>

The module exposes the OpenMRS API as REST web services. If an OpenMRS instance is running the `webservice.rest` module, other applications can retrieve and post certain information to an OpenMRS database. Module documentation is available at the [REST Module wiki page](https://wiki.openmrs.org/display/docs/REST+Module). Technical documentation for the module can be found [here](https://wiki.openmrs.org/display/docs/REST+Web+Services+Technical+Documentation).

## Download
  * [Download Module](https://modules.openmrs.org/#/show/153/webservices-rest)            
  * [Build from source](https://github.com/openmrs/openmrs-module-webservices.rest)

## Required OpenMRS Version

The REST Web Services module requires at least OpenMRS 1.9.0 or 1.8.1 to run.

## Developer Documentation

### API Documentation

The API documentation is available inside the OpenMRS application and is linked
to from the advanced administration screen.

### Example Client code
  * Quick java swing client that displays patients and encounters: http://svn.openmrs.org/openmrs-contrib/examples/webservices/hackyswingexample/
  * You can download a client java application that allows  add/edit a person (any resource) by making a query to the webservices.rest module - https://project-development-software-victor-aravena.googlecode.com/svn/trunk/ClientOpenMRSRest/

### For Creating Web Services in Core or Modules

  * [Adding a Web Service Step by Step Guide for Core Developers](https://wiki.openmrs.org/display/docs/Adding+a+Web+Service+Step+by+Step+Guide+for+Core+Developers)        
  * [Adding a Web Service Step by Step Guide for Module Developers](https://wiki.openmrs.org/display/docs/Adding+a+Web+Service+Step+by+Step+Guide+for+Module+Developers)

### Contributing to the API Documentation

The OpenMRS API documentation is built automatically using [Swagger UI](http://swagger.io/swagger-ui/). For details on how to customize the documentation see the [`swagger-ui` branch](../../tree/swagger-ui).

## License

[MPL 2.0](http://openmrs.org/license/)
