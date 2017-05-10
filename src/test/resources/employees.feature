Feature: Employees repository

  Scenario: client makes call to GET /employees
    When the client calls /employees
    Then the client receives status code of 200
    And the response should contain data:
    """
    { "name" : "Luke", "lastName" : "Skywalker", "dni" : "066778899R", "_links" : { "self" : { "href" : "http://localhost:8080/employees/1" }, "employee" : { "href" : "http://localhost:8080/employees/1" } } }
    """

  Scenario: create a employee
    When the client request POST "/employees" with json data
    """
    {"name" : "Boba", "lastName" : "Fett", "dni" : "088887777R"}
    """
    Then the client receives status code of 201