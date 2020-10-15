./init.sh

kubectl exec -ti sentry-web-deployment --namespace=sentry bash
sentry upgrade