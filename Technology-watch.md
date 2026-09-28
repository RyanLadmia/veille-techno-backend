# Technology watch

## 1. The frameworks

### NestJS

NestJS is a TypeScript framework for Node.js, strongly inspired by Angular and Spring. It uses modules, dependency injection, controllers, providers, guards and interceptors. It uses Express under the hood.

**Advantages:**

- TypeScript can be used for both frontend and backend.
- Fast to develop if the team already knows JavaScript/TypeScript.
- Large npm ecosystem.
- Good for REST APIs and real-time applications.
- Its architecture is easy to organise for a large project and has a great freedom.

**Disadvantages:**

- Node.js uses a single event loop, so CPU-heavy tasks can require workers or another service (mono thread).
- It has less experience than Java in large enterprise projects.
- The quality of npm packages can vary (and vulnerabilities).

For a small team that already knows TypeScript, NestJS can make development faster. The learning curve is also easier for me because I already used NestJS in previous projects.


---

### Symfony

Symfony is a mature PHP framework built around reusable components. It uses MVC, dependency injection, a service container and routing. It also works well with Doctrine ORM.

**Advantages:**

- Mature framework.
- Good documentation.
- Large PHP community.
- Good for web applications, CMS and e-commerce.
- Doctrine is a powerful ORM.
- Components can be used independently.

**Disadvantages:**

- PHP and Java have different ways of handling long-running applications.
- Caching can be important to improve performance.
- Doctrine has a learning curve similar to JPA.

For a Kanban API with authentication, ownership rules and CRUD, Symfony could be a good choice, especially for a team that already knows PHP.

I already had some experience with Symfony. I found its architecture interesting, especially Doctrine, but I was more comfortable with NestJS.


---

### Spring Boot

Spring Boot is a Java framework used to build backend applications and REST APIs. It provides dependency injection, MVC, security, database access, validation and testing tools.

**Advantages:**

- Mature ecosystem.
- Widely used in companies.
- Large amount of documentation and resources.
- Strong tools for security and database access.
- Good support for REST APIs and testing.
- Java applications can run on different operating systems and cloud environments.

**Disadvantages:**

- Higher learning curve, especially because I also had to learn Java.
- Higher memory usage than Node.js.
- Slower startup than Node.js.

During this project, I had to learn both Java and Spring Boot at the same time. The beginning was slower than with NestJS, especially for understanding Java syntax, Maven, Spring beans and dependency injection.

Once these concepts were understood, development became easier.

Spring Boot is well suited to this project because it provides the tools needed for JWT authentication, access control, database management, validation and testing.


---

## 2. What framework to choose?

Java with Spring Boot was chosen for this project mainly for two reasons: to learn a new technology and to develop skills that are useful for my professional goals.

I have already worked with NestJS and Symfony. Using one of these frameworks would have allowed me to develop the API faster, but it would have given me fewer opportunities to learn new things.

Java allows me to discover a new programming language, its strong typing, collections and exception handling. Spring Boot also allows me to discover a new backend ecosystem and use dependency injection, the MVC pattern and REST APIs.

Spring Boot is also well suited to the project's needs:

- REST API
- Database management
- JWT authentication
- Access control
- Data validation
- Testing

If the project is intended to grow and become more complex, then Java and Spring Boot are the most logical choices.

During the first days of the project, the main difficulty was learning Java and Spring Boot at the same time. I also had to understand Maven, Spring beans, dependency injection and the Spring project structure.

My previous experience with NestJS helped me because some concepts, such as dependency injection and controllers, are similar.

Finally, Java and Spring Boot are widely used in companies. This first experience will allow me to expand my skills and improve my profile for my search for an apprenticeship.

The main disadvantage is the higher learning curve compared to NestJS for me. However, this is acceptable because learning a new technology is also one of the main goals of the project.

---

## Which version of Java?

Java 27 is the latest version of the language. It was released in September 2026, so it is still a new version.

JDK 25 is the latest **LTS (Long-Term Support)** version of Java. An LTS version receives long-term support and is a better choice for a project that needs a stable version over several years.

I chose **JDK 25** for this project.

The project uses **Spring Boot 4.1.1** with Java 25.


---

## Maven vs Gradle

Maven and Gradle are build tools for Java projects. They manage dependencies, compile the project, run tests and build the application.

### Maven

Maven is commonly used for Java and Spring Boot projects. It follows standard conventions and uses a `pom.xml` file to configure the project.

**Advantages:**

- Simple and predictable structure.
- Mature and widely used.
- Easy for a team to understand.
- Uses standard conventions.
- Good integration with Spring Boot.

**Disadvantages:**

- Less flexible than Gradle.
- Custom build configurations can be more verbose.

Maven is a good choice for a project where I want a simple and standard configuration. It also makes the project easier for another Java developer to understand.

### Gradle

Gradle is another build tool for Java projects. It is more flexible than Maven and allows more customisation of the build process.

It is also widely used for Android projects, but it is not limited to Android.

**Advantages:**

- Very flexible.
- Good support for custom build processes.
- Can provide faster builds in some projects.
- Used in Java and Android projects.

**Disadvantages:**

- More configuration possibilities can make projects harder to understand.
- The build configuration can be more complex for a beginner.

For this project, I chose **Maven** because it is simple, mature and widely used with Java and Spring Boot. Its standard structure is also easier for me to understand while learning Java.

---

## 3. Learning curve & team ramp-up

The learning curve was different for each framework.

For me, **NestJS was the easiest** because I already knew TypeScript and had used NestJS before. I already understood concepts such as dependency injection, controllers, services and modules.

Symfony was also easier than Spring Boot because I already had some experience with PHP and Symfony.

Spring Boot was the most challenging.

The main difficulties were:

- Java syntax and its type system.
- Maven and the `pom.xml`.
- Spring beans.
- Dependency injection.
- JPA and Hibernate.
- Understanding how Spring manages the application.

My previous knowledge of NestJS helped me understand some Spring concepts because the architectures are similar.
I also found similarities with Symfony and Doctrine, particularly in the declaration of getters and setters.

For a team, the learning curve depends strongly on the developers' previous experience. A Java team would probably start much faster with Spring Boot, while a TypeScript team would probably start faster with NestJS.

After the first learning period, Spring Boot became easier to use because the project follows standard conventions.

---

## 4. Shared principles & patterns used in this codebase

The project uses a classic **MVC / layered architecture**.

The main parts are:

- **Controller:** receives HTTP requests and returns HTTP responses.
- **Service:** contains the application's business logic.
- **Repository:** communicates with the database.
- **Entity:** represents tables in the database.
- **DTO:** defines the contract of the data received or returned by the API.

I also use **Dependency Injection**. Spring creates and injects the required objects instead of creating them manually.

For example, a controller can receive a service through its constructor:

```java
public AuthController(AuthService authService) {
		this.authService = authService;
	}
```

This keeps the different parts of the application separated and makes the code easier to test.

The project also uses:

- REST principles.
- DTOs for API data.
- JPA/Hibernate for database access.
- JWT for authentication.
- BCrypt for password hashing.
- Validation for incoming data.
- HTTP status codes to communicate the result of requests.

I chose a classic MVC architecture instead of Clean Architecture or Hexagonal Architecture because the project is relatively small and my main goal is to learn Java and Spring Boot (KISS and YAGNI).

---

## 5. Real-world efficiency, portability, maturity

Spring Boot is well suited to professional backend applications.

For this project, it provides most of the tools I need without having to build them myself.

The Java ecosystem is also mature and has been used in professional applications for many years.

Java applications are portable because the JVM allows the same application to run on different operating systems.

Spring Boot can also be deployed on traditional servers, containers and cloud platforms. This makes it compatible with a Docker-based deployment.

However, Spring Boot can require more memory and have a higher startup time than Node.js applications. For a small application, this can be an important point.

Spring Boot is therefore a good fit for applications where maintainability, a mature ecosystem and a strong backend architecture are important.

---

## 6. Maintainability plan

The main goal is to keep the project simple and easy to understand.

I use the standard Maven project structure and separate the main responsibilities between controllers, services, repositories, entities and DTOs.

For maintenance, I would:

- Keep Java and Spring Boot versions updated.
- Check Spring Boot release notes before updating.
- Update dependencies 
- Keep tests updated when the code changes.
- Use automated tests for important business rules.
- Keep the code and API documentation up to date.
- Avoid unnecessary dependencies.

I also want to avoid strong coupling with external libraries when they are not necessary.

For example, Spring Boot, JPA and Hibernate are useful for this project, but the application should not depend on unnecessary libraries.

The main risk is that Spring has a large ecosystem. It is therefore important to understand which dependencies are really needed and to keep them updated.

Also, Java itself and Spring Boot have a main update every six months. So I would make sure to user the LTS.
Maven is updated much more often. It takes years for a new update.

Therefore, we need to stay informed and check for new stable and reliable versions at least every six months, or once a year.

---

## 7. Eco-design

Eco-design was not the main factor when choosing the framework.

The main objective of the project was to learn Java and Spring Boot and compare backend technologies.

I have not yet implemented protocols to ensure eco-responsibility, but if the application were to become more complex or evolve significantly, we could implement the following optimizations:

- Keep the API simple.
- Avoid unnecessary requests.
- Return only the required data.
- Use pagination for large amounts of data.
- Optimise database queries.
- Avoid unnecessary dependencies.
- Keep using Docker to keep the deployment reproducible.
- Monitor resource consumption when the application is deployed.

Spring Boot can use more memory than a small Node.js application, especially with a JVM application. This is therefore one of the points to consider when choosing the technology for a very small application.

For this Kanban project, the difference is not the main decision factor because the application is small.

---

## 8. Paradigm comparison

NestJS, Symfony and Spring Boot have different ecosystems, but they share many concepts.

The three frameworks also use similar architectural ideas:

- Controllers handle HTTP requests.
- Services contain application logic.
- Dependency Injection separates components.
- ORM tools simplify database access.
- Validation protects the API.
- Middleware, filters, guards or interceptors can control requests.