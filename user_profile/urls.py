from django.conf.urls import url,include
from django.urls import path
from . import views
urlpatterns = [
    path('follow', views.follow),
    path('check_i_follow', views.check_i_follow),
    path('followers', views.followers),

]