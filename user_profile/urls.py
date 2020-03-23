from django.conf.urls import url,include
from django.urls import path
from tv_shows.views import login_redirection

from . import views
urlpatterns = [
    path('follow', views.follow),


]