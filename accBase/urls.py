from django.conf.urls import url

from . import views
urlpatterns = [
    url(r'^registration', views.registration, name='registration'),
    url(r'^login', views.login, name='login'),
    url(r'^logout', views.logout, name='logout'),
    url(r'^forgot_pass', views.forgot_pass, name='forgot_pass'),

]