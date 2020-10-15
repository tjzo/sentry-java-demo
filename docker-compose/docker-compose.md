```bash
docker-compose up -d
docker exec -ti sentry9_test_1 bash
sentry --config /etc/sentry/ upgrade
sentry --config /etc/sentry/ config set system.url-prefix https://sentry-jst.woda.com
```