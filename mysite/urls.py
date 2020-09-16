
from django.conf.urls import include, url
from django.contrib import admin
from django.urls import path

urlpatterns = [
    url(r'acc_base/', include('acc_base.urls')),
    url(r'user_profile/', include('user_profile.urls')),
    url(r'tv_shows/', include('tv_shows.urls')),
    url(r'home/', include('home.urls')),
    url(r'chatroom/', include('chatroom.urls')),
    #url(r'^admin/', admin.site.urls),
    url(r'map/', include('maps.urls')),
]
