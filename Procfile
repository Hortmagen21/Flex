release: python manage.py migrate --noinput
web: daphne mysite.asgi:application --port $PORT --bind 0.0.0.0
worker: REMAP_SIGTERM=SIGQUIT celery worker --app mysite.celery.app --loglevel info