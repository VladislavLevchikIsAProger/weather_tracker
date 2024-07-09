# Используем официальный образ Tomcat 10 с JDK 17
FROM tomcat:10.1.10-jdk17

# Указываем рабочую директорию в контейнере
WORKDIR /usr/local/tomcat

# Удаляем стандартное приложение Tomcat
RUN rm -rf webapps/*

# Копируем ваш ROOT.war файл в контейнер
COPY ROOT.war webapps/

# Открываем порт 8080 для доступа к Tomcat
EXPOSE 8080

# Запускаем Tomcat
CMD ["catalina.sh", "run"]