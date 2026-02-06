# Backend

This app was created with Bootify.io - tips on working with the code [can be found here](https://bootify.io/next-steps/).

## Development

Update your local database connection in `application.yml` or create your own `application-local.yml` file to override settings for development.

During development it is recommended to use the profile `local`. In IntelliJ `-Dspring.profiles.active=local` can be added in the VM options of the Run Configuration after enabling this property in "Modify options".

Lombok must be supported by your IDE. For IntelliJ install the Lombok plugin and enable annotation processing - [learn more](https://bootify.io/next-steps/spring-boot-with-lombok.html).

In addition to the Spring Boot application, the DevServer must also be started - for this [Node.js](https://nodejs.org/) version 24 is required. On first usage and after updates the dependencies have to be installed:

```
npm install
```

The DevServer can be started as follows:

```
npm run devserver
```

Using a proxy the whole application is now accessible under `localhost:3000`. All changes to the templates and JS/CSS files are immediately visible in the browser. Frontend unit tests can be executed with `npm run test`.

## Build

The application can be built using the following command:

```
gradlew clean build
```

Node.js is automatically downloaded using the `gradle-node-plugin` and the final JS/CSS files are integrated into the jar.

Start your application with the following command - here with the profile `production`:

```
java -Dspring.profiles.active=production -jar ./build/libs/backend-0.0.1-SNAPSHOT.jar
```

If required, a Docker image can be created with the Spring Boot plugin. Add `SPRING_PROFILES_ACTIVE=production` as environment variable when running the container.

```
gradlew bootBuildImage --imageName=com.aiw/backend
```



## Further readings

* [Gradle user manual](https://docs.gradle.org/)  
* [Spring Boot reference](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)  
* [Spring Data JPA reference](https://docs.spring.io/spring-data/jpa/reference/jpa.html)
* [Learn React](https://react.dev/learn)
* [Webpack concepts](https://webpack.js.org/concepts/)  
* [npm docs](https://docs.npmjs.com/)  
* [Tailwind CSS](https://tailwindcss.com/)

---

### 🧾 코드 컨벤션

커밋 메시지는 다음 규칙에 맞춰 작성합니다

> 📌 예시: **`✨ feat: sign up complete`**

---

| 태그                | 설명                                      |
|-------------------|-----------------------------------------|
| ✨ **feat**        | 새로운 기능 추가                               |
| 🐛 **fix**        | 버그 수정                                   |
| 📝 **docs**       | 문서 수정 (README 등)                        |
| 💄 **style**      | 코드 포맷팅, 세미콜론 누락, 코드 변경 없음               |
| ♻️ **refactor**   | 코드 리팩토링 (기능 변화 없이 구조 개선)                |
| ✅ **test**        | 테스트 코드 추가, 기존 테스트 리팩토링                  |
| 🔧 **chore**      | 빌드 설정 변경, 패키지 매니저 설정 등                  |
| 🔀 **merge**      | 브랜치 병합 (예: `merge: main` → main과 병합했음) |
| 📍 **checkpoint** | 진행중(체크포인트)                              |
| 🎨 **design**     | 뷰 디자인 변경                                |

---
