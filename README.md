# Mail Delivery Application
>The system registers postal items - letters, parcels - their movement between post offices, as well as provides information on the entire history of movement of a particular postal item.

## Used technologies
* **Java**
* Spring Boot, Spring Data JPA (to work with entities)
* **PostgreSQL** (main DBMS for this project)
* Maven (package manager to manipulate with dependecies)
* Flyway(for migration with database)
* Lombok 
* ModelMapper (for mapping models to and from dto)
* JUnit5 and Mockito (for testing)

## Steps to Setup

**1. Clone the application**

```bash
git clone https://github.com/notas4int/test-task-mail-delivery.git
```

**2. Create PostgreSQL database**
```bash
create database mail-delivery
```

**3. Change PostreSQL username and password as per your installation**

+ open `src/main/resources/application.properties`
+ change `spring.datasource.username` and `spring.datasource.password` as per your PostgreSQL installation

**4. Run the app using maven**

```bash
mvn spring-boot:run
```
The app will start running at <http://localhost:8080>

## Explore Rest APIs

### Users
| Method | Url                                 | Decription                                                            | Sample Valid Request Type | 
|--------|-------------------------------------|-----------------------------------------------------------------------|---------------------------|
| POST   | /api/mail/register-postal-item      | add new postal item                                                   | registrationRequest       |
| PATCH  | /api/mail/add-post-office           | add post office and change status to 'in_the_post_office'             | postOfficeAddingRequest   |
| PATCH  | /api/mail/left-postal-item/{id}     | change status to 'en_route' by id                                     |                           |
| GET    | /api/mail/check-postal-item/{id}    | get status and movement history by id and return  postalItemResponse  |                           |
| PATCH  | /api/mail/receive-postal-item/{id}  | change status to 'receive' by id                                      |                           |

##### <a>registrationRequest</a>
```json
{
  "mailType": "PARCEL",
  "personIndex": 342,
  "address": "Mira 23",
  "personName": "Vova",
  "postOfficeId": 1
}
```

##### <a> postOfficeAddingRequest </a>
```json
{
  "id": 1,
  "postOfficeId": 2
}
```

##### example of <a>postalItemResponse</a>

```json
{
  "mailStatus": "EN_ROUTE",
  "postOffices": [
    {
      "id": 2,
      "name": "Post office the second",
      "address": "Veteranov 3"
    },
    {
      "id": 1,
      "name": "Post office first",
      "address": "Mira 23"
    }
  ]
}
```
## Coverage
#### unit test coverage
![](src/test/resources/coverage%20screen/unit-test-coverage.png)
#### integration test coverage
![](src/test/resources/coverage%20screen/it-test-coverage.png)