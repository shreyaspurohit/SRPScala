SRP Scala
=========

This library provides an implementation of secure remote protocol in scala. The SRP version implemented is 6.a as provided at http://srp.stanford.edu. The design details are provided at http://srp.stanford.edu/design.html. There are comphrensive suite of test cases provided to cover many possible cases.

Build
-----

Clone the repo. The build script is provided in sbt. Run clean, compile, test, package commands to generate the jar output file. 

Example
-------

The example folder consists of an example to use the library. The example currently is provided in Play 2.0. Please look at Readme.txt present in the example folder. Just run the application, you can register and go to login page to see SRP in action. Example UN/PW are already registered: shreyas/shreyas. You can see it live in action at http://srp.bitourea.com (A little modified version with memory to save instead of File I/O for storing data). 

Documentation
-------------

Look at the Readme.txt in the example folder to use the library. To understand SRP, please go through http://srp.stanford.edu/.

Latest version
--------------

The jar has not yet been published to any external repository to be used with sbt, gradle or any other build tool, but is planned to do so soon.

Client (Javascript) side
------------------------

Both server and client side library is provided. The client side is defined in srp.js present in web/js folder. The dependencies that are required for client side to work are packages.js, binary.js, BigInteger.init1.js, BigInteger.init2.js, BigInteger.init3.js, jsSHA.js, SHA.js, and SecureRandom.js. All are provided in the web/js/titaniumcore folder. The license to use these libraries are provided in web/js/titaniumcore/license.txt. For example usage look at the provided example.

Licensing
---------

Released under MIT license, go ahead and use, modify, distribute as you wish. The license is provided in LICENSE.txt. The license of other libraries used must be used as defined by them. 

