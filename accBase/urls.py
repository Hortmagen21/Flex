from django.conf.urls import url,include
from django.urls import path

from . import views
urlpatterns = [
    path('registration', views.registration),
    path('registration/ended', views.verifying),
    path('login', views.login),
    path('logout', views.logout),
    path('forgot_pass', views.forgot_pass),
    path('reset_pass', views.reset_pass),

]
