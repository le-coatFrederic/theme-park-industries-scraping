FROM maven:3.9-eclipse-temurin-25-alpine AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:25-jre

WORKDIR /app

# Install Chrome and required dependencies for Selenium
RUN apt-get update && apt-get install -y --no-install-recommends \
    wget \
    gnupg \
    ca-certificates \
    fonts-liberation \
    libnss3 \
    lsb-release \
    xdg-utils \
    && rm -rf /var/lib/apt/lists/*

# Install Google Chrome
RUN wget -q -O - https://dl-ssl.google.com/linux/linux_signing_key.pub | gpg --dearmor -o /usr/share/keyrings/google-chrome-archive-keyring.gpg && \
    echo "deb [signed-by=/usr/share/keyrings/google-chrome-archive-keyring.gpg] http://dl.google.com/linux/chrome/deb/ stable main" | tee /etc/apt/sources.list.d/google-chrome.list && \
    apt-get update && \
    apt-get install -y --no-install-recommends google-chrome-stable && \
    rm -rf /var/lib/apt/lists/*

# Create spring user and directories
RUN useradd -m -s /bin/bash spring && \
    mkdir -p /home/spring/.wdm && \
    chown -R spring:spring /home/spring && \
    chmod 777 /tmp

USER spring:spring

COPY --from=build /app/target/*.jar app.jar

# Set WebDriverManager cache directory
ENV WDM_HOME=/home/spring/.wdm

ENTRYPOINT ["java","-jar","app.jar"]