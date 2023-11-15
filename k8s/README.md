# Инструкция развертывания в Kubernetes
Для развертывания в k8s необходимо:
1. Задать значения переменным среды `SPRING_MAIL_USERNAME` и `SPRING_MAIL_PASSWORD` контейнера `app` в [app-deployment.yaml](app-deployment.yaml) для работы SMTP сервера.
2. Выполнить команду: `kubectl apply -f app-deployment.yaml -f nginx-deployment.yaml -f statefulset-postgres.yaml`.