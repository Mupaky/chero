🕹️ Underground Guardian

Underground Guardian is a turn-based MVC project built with Java 17, Spring Boot, JPA,and Thymeleaf,
featuring a RESTful API for handling game logic and additional services like email notifications.
Players assume the role of brave Guardians defending humanity from terrifying creatures lurking beneath the surface.

Game Overview
  Beneath the city lies a forgotten world filled with darkness and monstrous beings.
  As one of the last Guardians, you must fight, train, and grow stronger to survive.
  Venture into the Underground, collect resources, level up, and prove your worth.

Tech Stack - Layer Technology
  Language Java 23
  Build Tool Gradle
  Framework Spring Boot 3.4.2
  Cloud Integration Spring Cloud with OpenFeign
  Security Spring Security 6
  Templating Thymeleaf with Spring Security integration
  ORM and DB Spring Data JPA with MySQL
  Validation Jakarta Validation
  Monitoring Spring Boot Actuator
  API Style RESTful
  UI Server-rendered using Thymeleaf templates
  Dependency Management Spring Dependency Management Plugin
  Async Communication Feign Clients for external service calls like mail sending
  Lombok Used for boilerplate code generation
  Dev Tools Spring Boot Devtools for live reloading during development
  Testing JUnit 5 and Spring Security Test

Project Structure

1.GameCharacter.class

  Represents the player’s avatar and tracks progression.

  Attributes include experience, level, health, energy, nickName, etc Relations:

  One-to-One with User, Inventory, and Stats.

  Equipped items via wearings map of slot to item.

  Resources stored in an EnumMap e.g. GOLD, WOOD.

2.Inventory.class

  Holds a character’s item collection.

  Fields:

  Linked to GameCharacter.

  List of Item entities.

3.Shop.class

  Global shop entity with items for sale.

  Fields:

  List of purchasable Items.

4.User.class

  Represents a real-world player.

  Fields include username, email, password, country, userRole, isActive Linked to one GameCharacter Tracks subscriptions and notifications.

  Mail Service Other Module or Project.

  A dedicated REST API handles email notifications such as password recovery, welcome emails.

  Tech Spring Boot with JavaMailSender Integration Via internal REST calls from the game backend Security Can be protected using token-based auth if needed.



- Game Mechanics

  Combat

  Easy 70 percent, Medium 50 percent, Hard 40 percent win probabilities. Rewards Gold and EXP, defeat HP reduction.

- Progression

  Characters level up by gaining EXP. Level-up triggers custom notifications.

- Resource Handling

  Stored in a map Map of ResourceType to Integer. Resources are used for purchases, upgrades, or crafting in future scope.

Lore-driven intro to the Underground world.
In-game UI for selecting and initiating challenges.
Displays monsters, reward tiers, and real-time fight results.
Dynamic content rendering via Thymeleaf with form-based fight triggers.



How to Run

  Requirements Java 23, Gradle, MySQL

  Clone and Run

  git clone https colon slash slash github dot com slash your-repo slash underground-guardian dot git
  cd underground-guardian
  ./mvnw spring-boot colon run
  
  Configuration application.properties

  spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
  spring.datasource.url equals jdbc colon postgresql colon slash slash localhost colon 5432 slash guardian
  spring.datasource.username equals your_db_user
  spring.datasource.password equals your_password
  spring.jpa.hibernate.ddl-auto equals update
  spring.security.user.name=
  spring.security.user.password=
  spring.web.resources.static-locations=file:src/main/resources/static/

  server.port=8082 change to your desire port
