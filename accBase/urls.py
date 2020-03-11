from django.conf.urls import url

from . import views
urlpatterns = [
    url(r'^registration', views.registration, name='registration'),
    url(r'^registration/ended', views.verifying, name='verifying'),
    url(r'^login', views.login, name='login'),
    url(r'^logout', views.logout, name='logout'),
    url(r'^forgot_pass', views.forgot_pass, name='forgot_pass'),
    url(r'^reset_pass', views.reset_pass, name='reset_pass'),

]
