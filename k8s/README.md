# Инструкция развертывания в Kubernetes
Для развертывания в k8s необходимо:
1. Для Secret задайте учетные данные (`username` и `password`) в файле [app-deployment.yaml](app-deployment.yaml) для аутентификации в GitLab Registry.  
2. Задать значения переменным среды `SPRING_MAIL_USERNAME` и `SPRING_MAIL_PASSWORD` контейнера `app` в [app-deployment.yaml](app-deployment.yaml) для работы SMTP сервера.
3. Выполнить команду: `kubectl apply -f app-deployment.yaml -f nginx-deployment.yaml -f statefulset-postgres.yaml`.
4. После применения манифестов k8s приложение будет доступно по адресу `govservice.com`.