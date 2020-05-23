"""
Django settings for mysite project.

Generated by 'django-admin startproject' using Django 3.0.3.

For more information on this file, see
https://docs.djangoproject.com/en/3.0/topics/settings/

For the full list of settings and their values, see
https://docs.djangoproject.com/en/3.0/ref/settings/
"""
import os
import django
import django_heroku



# Build paths inside the project like this: os.path.join(BASE_DIR, ...)
BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))

# Quick-start development settings - unsuitable for production
# See https://docs.djangoproject.com/en/3.0/howto/deployment/checklist/

# SECURITY WARNING: keep the secret key used in production secret!
SECRET_KEY = '7alx^(*xh($*^^7&+1!9q2_g66-tnhh(3ta@^!0q^3v4-r(a!o'


# SECURITY WARNING: don't run with debug turned on in production!
DEBUG = True

ALLOWED_HOSTS = []


# Application definition

INSTALLED_APPS = [
    'rest_framework',
    'django.contrib.admin',
    'django.contrib.auth',
    'django.contrib.contenttypes',
    'django.contrib.sessions',
    'django.contrib.messages',
    'django.contrib.staticfiles',
    'acc_base.apps.AccConfig',
    'tv_shows.apps.TvShowsConfig',
    'home.apps.HomeConfig',
    'chatroom.apps.ChatroomConfig',
    'user_profile.apps.UserProfileConfig',
    'channels',
    "fcm_django",

    #'mysite.polls.apps.PollsConfig',
    #'mysite.polls',
]
FCM_DJANGO_SETTINGS = {
    "FCM_SERVER_KEY": "AAAAVQJ_SoU:APA91bFWua6OATBhXUCZdTGiRWBg_af-3H4wrLmBBBC8dcPzzpacSg8HYbm3YUYTGiK9sLgU-Dm5-IxgSIxHOSMSNq7o-NQXW37QWX5gykQzNGr7USXfm1HpRZnAkcF4hvbFi0Dk9lEn",

    "ONE_DEVICE_PER_USER": False,

    "DELETE_INACTIVE_DEVICES": True,
}



CACHES = {
    "default": {
        "BACKEND": "django_redis.cache.RedisCache",
        "LOCATION": os.getenv('REDIS_URL'),#!!
        #"LOCATION": os.environ['REDIS_URL'],  # Here we have Redis DSN (for ex. redis://localhost:6379/1)
        "OPTIONS": {
            "CLIENT_CLASS": "django_redis.client.DefaultClient",
            "MAX_ENTRIES": 1000  # Increase max cache entries to 1k (from 300)
        },
    }
}
#!CELERY_BROKER_URL = os.environ['REDIS_URL']
#!CELERY_RESULT_BACKEND = os.environ['REDIS_URL']
CELERY_BROKER_URL = os.getenv('REDIS_URL')
CELERY_RESULT_BACKEND = os.getenv('REDIS_URL')

ASGI_APPLICATION = 'mysite.routing.application'

MIDDLEWARE = [
    'django.middleware.security.SecurityMiddleware',
    'django.contrib.sessions.middleware.SessionMiddleware',
    'django.middleware.common.CommonMiddleware',
    'django.middleware.csrf.CsrfViewMiddleware',
    'django.contrib.auth.middleware.AuthenticationMiddleware',
    'django.contrib.messages.middleware.MessageMiddleware',
    'django.middleware.clickjacking.XFrameOptionsMiddleware',
]

ROOT_URLCONF = 'mysite.urls'

TEMPLATES = [
    {
        'BACKEND': 'django.template.backends.django.DjangoTemplates',
        'DIRS': [],
        'APP_DIRS': True,
        'OPTIONS': {
            'context_processors': [
                'django.template.context_processors.debug',
                'django.template.context_processors.request',
                'django.contrib.auth.context_processors.auth',
                'django.contrib.messages.context_processors.messages',
            ],
        },
    },
]

WSGI_APPLICATION = 'mysite.wsgi.application'


# Database
# https://docs.djangoproject.com/en/3.0/ref/settings/#databases

DATABASES = {
    'default': {
        'ENGINE': 'django.db.backends.postgresql',
        'NAME': 'd7f6m0it9u59pk',
        'USER': 'iffjnrmpbopayf',
        'PASSWORD': '20d31f747b4397c839a05d6d70d2decd02b23a689d86773a84d8dcfa23428946',
        'HOST': 'ec2-54-83-1-101.compute-1.amazonaws.com',
        'PORT': '5432',
    }
}


# Password validation
# https://docs.djangoproject.com/en/3.0/ref/settings/#auth-password-validators

AUTH_PASSWORD_VALIDATORS = [
    {
        'NAME': 'django.contrib.auth.password_validation.UserAttributeSimilarityValidator',
    },
    {
        'NAME': 'django.contrib.auth.password_validation.MinimumLengthValidator',
    },
    {
        'NAME': 'django.contrib.auth.password_validation.CommonPasswordValidator',
    },
    {
        'NAME': 'django.contrib.auth.password_validation.NumericPasswordValidator',
    },
]


# Internationalization
# https://docs.djangoproject.com/en/3.0/topics/i18n/

LANGUAGE_CODE = 'en-us'

TIME_ZONE = 'UTC'

USE_I18N = True

USE_L10N = True

USE_TZ = True


# Static files (CSS, JavaScript, Images)
# https://docs.djangoproject.com/en/3.0/howto/static-files/

STATIC_URL = '/static/'

CHANNEL_LAYERS = {
    'default': {
        'BACKEND': 'channels_redis.core.RedisChannelLayer',
        'CONFIG': {
            #!"hosts": [os.environ['REDIS_URL']],
            "hosts": [os.getenv('REDIS_URL')],
        },
    },
}
django.setup()





django_heroku.settings(locals())

EMAIL_HOST = 'smtp.mail.yahoo.com'#'smtp.gmail.com'
EMAIL_PORT =587#465
EMAIL_HOST_USER = 'flexsender@yahoo.com'#'hortmagennn@gmail.com'
EMAIL_HOST_PASSWORD = 'rexwex21'
EMAIL_USE_TLS = True#False
EMAIL_USE_SSL = False#True








